{
  network = {
    storage.memory = {};
  };

  www = let
    domain = "sailvision.net";
    host   = "www";

    adminIP = "172.62.4.1";
    vpnPort = 63532;

    admins = {
      alex.dali = {
        publicKey  = "PmI3r4hottDbIt0kq/IEuGU6g5Zi89qZonS6XbzLrlY=";
        allowedIPs = [ "172.62.4.16/32" ];
      };
      alex.timapple = {
        publicKey  = "I5RcHamTFyP4nE+tQ2CFpQ3qY2VmC+YEi77SsxwzcQQ=";
        allowedIPs = [ "172.62.4.17/32" ];
      };
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

        certs."wildcard.i.${domain}" = {
          environmentFile = "/etc/secrets/digitalocean.env";
          dnsProvider     = "digitalocean";
          domain          = "*.i.${domain}";
          group           = config.services.nginx.user;
        };
      };

      services = {
        nginx = {
          enable = true;

          recommendedOptimisation  = true;
          recommendedProxySettings = true;
          recommendedTlsSettings   = true;

          clientMaxBodySize = "50m";

          virtualHosts = {
            "www.i.${domain}" = {
              useACMEHost = "wildcard.i.${domain}";
              forceSSL    = true;

              listenAddresses = [ adminIP ];

              locations."/status".extraConfig = "stub_status;";
            };

            "${domain}" = {
              enableACME     = true;
              forceSSL       = true;
              globalRedirect = "www.${domain}";
            };

            "www.${domain}" = {
              enableACME = true;
              forceSSL   = true;

              locations."/".proxyPass = "http://127.0.0.1:8080";

              extraConfig = ''
                proxy_intercept_errors     on;
                error_page 500 502 503 504 /5xx.html;
              '';
            };
          };
        };
      };

      systemd = let
        uberjar  = "net.sailvision.www-unversioned-standalone.jar";
        localBin = "/usr/local/bin";
        path     = "${localBin}/${uberjar}";
        nextPath = "/tmp/${uberjar}";
        stateDir = "www";
      in {
        services.www = {
          requiredBy = [ "multi-user.target" ];

          serviceConfig = {
            Environment = builtins.concatStringsSep " " [
              "NEXT_PATH=${nextPath}"
              "DB_STORAGE=/var/lib/${stateDir}/db"
            ];

            User  = config.users.users.www.name;
            Group = config.users.users.www.group;

            LockPersonality       = true;
            NoNewPrivileges       = true;
            PrivateDevices        = true;
            PrivateTmp            = true;
            PrivateUsers          = true;
            ProtectClock          = true;
            ProtectControlGroups  = true;
            ProtectHome           = true;
            ProtectHostname       = true;
            ProtectKernelLogs     = true;
            ProtectKernelModules  = true;
            ProtectKernelTunables = true;
            ProtectSystem         = "strict";
            ReadWritePaths        = localBin;
            RestrictRealtime      = true;
            RestrictSUIDSGID      = true;
            StateDirectory        = stateDir;
            SystemCallFilter      = [ "@system-service" ];

            Restart      = "always";
            ExecStart    = "${pkgs.temurin-jre-bin}/bin/java -jar ${path}";
            ExecStopPost = let
              script = builtins.replaceStrings ["\n"] ["\\n"] ''
                if [ -f ${nextPath} ]
                then
                  mv ${nextPath} ${path}
                  echo Updated uberjar
                fi
              '';
            in "/bin/sh -e -c '${script}'";
          };
        };

        tmpfiles.rules = [
          "d ${localBin} 0775 root ${config.users.groups.www.name} - -"
        ];
      };

      users = {
        users.www = {
          group        = config.users.groups.www.name;
          isSystemUser = true;
        };

        groups.www = {};
      };

      zramSwap = {
        enable        = true;
        memoryPercent = 75;
      };
    };
}
