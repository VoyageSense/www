{
  network = {
    storage.memory = {};
  };

  www = let
    domain = "sailvisionpro.com";
    host   = "www";

    adminIP = "172.62.4.1";
    vpnPort = 63532;

    admins.alex.dali = {
      publicKey  = "PmI3r4hottDbIt0kq/IEuGU6g5Zi89qZonS6XbzLrlY=";
      allowedIPs = [ "172.62.4.16/32" ];
    };
  in
    { modulesPath, config, pkgs, ... }: {
      imports = [
        "${modulesPath}/virtualisation/digital-ocean-config.nix"
        "${modulesPath}/profiles/minimal.nix"
      ];

      nixpkgs.hostPlatform = "x86_64-linux";
      system.stateVersion  = "24.11";

      deployment = {
        targetHost = "${host}.${domain}";
        targetUser = "root";
      };

      # Config

      boot.tmp.cleanOnBoot = true;

      networking = {
        hostName = host;
        domain   = domain;

        useNetworkd = true;

        firewall.allowedTCPPorts = [
          config.services.nginx.defaultHTTPListenPort
          config.services.nginx.defaultSSLListenPort
        ];

        firewall.allowedUDPPorts = [
          config.networking.wireguard.interfaces.wg0.listenPort
        ];

        wireguard.interfaces.wg0 = {
          ips        = [ "${adminIP}/24" ];
          listenPort = vpnPort;

          # Public Key: 4e0bwu5q6fInesM6T3BCgxJ4exrzhG4wbL5vDqJ3gAM=
          privateKeyFile = "/var/lib/wireguard/private-key";

          peers = builtins.concatMap builtins.attrValues (builtins.attrValues admins);
        };
      };

      security.acme = {
        acceptTerms     = true;
        defaults.email  = "alex+letsencrypt.org@sailvisionpro.com";
        # defaults.server = "https://acme-staging-v02.api.letsencrypt.org/directory";
      };

      services = {
        nginx = {
          enable = true;

          recommendedOptimisation  = true;
          recommendedProxySettings = true;
          recommendedTlsSettings   = true;

          clientMaxBodySize = "50m";

          virtualHosts = {
            "www.i.sailvisionpro.com" = {
              listenAddresses = [ adminIP ];

              locations."/status".extraConfig = "stub_status;";
            };

            "sailvisionpro.com" = {
              enableACME     = true;
              forceSSL       = true;
              globalRedirect = "www.sailvisionpro.com";
            };

            "www.sailvisionpro.com" = {
              enableACME = true;
              forceSSL   = true;

              locations."/".proxyPass = "http://127.0.0.1:8080";
            };
          };
        };
      };

      systemd.services = let
        uberjar  = "com.sailvisionpro.www-unversioned-standalone.jar";
        path     = "/var/www/${uberjar}";
        nextPath = "/tmp/${uberjar}";
        dbPath   = "db";
      in {
        www = {
          requiredBy = [ "multi-user.target" ];

          serviceConfig = {
            Environment = ''
              NEXT_PATH=${nextPath}
              DB_STORAGE=${dbPath}
            '';
            Restart     = "always";

            ExecStart    = "${pkgs.temurin-jre-bin}/bin/java -jar ${path}";
            ExecStartPre = "-/bin/sh -c '[ -f ${nextPath} ] && mv ${nextPath} ${path}'";
          };
        };
      };

      zramSwap = {
        enable        = true;
        memoryPercent = 75;
      };
    };
}
