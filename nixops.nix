{
  network = {
    storage.memory = {};
  };

  www = let
    domain = "sailvisionpro.com";
    host   = "www";
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

        firewall.allowedTCPPorts = [ 80 443 ];
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
            Environment = "NEXT_PATH=${nextPath}";
            Environment = "DB_STORAGE=${dbPath}";
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
