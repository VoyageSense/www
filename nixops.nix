# Copyright 2025 PopaiTheSailor Authors
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with this program.  If not, see <https://www.gnu.org/licenses/>.

{
  network = {
    storage.memory = {};
  };

  www = let
    domain = "popaithesailor.com";
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
      george.macbookpro = {
        publicKey  = "oOnzj7XPII3fe46n4IvTw3IxSs7UEDBXT0ESuEHzf0k=";
        allowedIPs = [ "172.62.4.18/32" ];
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
        defaults.email  = "letsencrypt.org@alex.voyagesense.com";
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

          # Needed for PostHog proxy
          resolver.addresses = [
            "8.8.8.8"
            "8.8.4.4"
          ];

          virtualHosts = {
            "sailvisionpro.com".globalRedirect     = domain;
            "www.sailvisionpro.com".globalRedirect = "www.${domain}";

            "www.i.${domain}" = {
              useACMEHost = "wildcard.i.${domain}";
              forceSSL    = true;

              listenAddresses = [ adminIP ];

              locations = {
                "/status".extraConfig = "stub_status;";
                "/".proxyPass         = "http://127.0.0.1:9080";
              };
            };

            # Adapted from https://posthog.com/docs/advanced/proxy/nginx
            "ph.${domain}" = {
              enableACME = true;
              forceSSL   = true;

              locations = {
                "~ ^/static/(.*)$".extraConfig = ''
                  set $posthog_static "https://us-assets.i.posthog.com/static/";
                  # use variable to force proper DNS re-resolution, also must manually pass along path
                  proxy_pass $posthog_static$1$is_args$args;
                  proxy_set_header Host "us-assets.i.posthog.com";
                '';

                "~ ^/(.*)$".extraConfig = ''
                  set $posthog_main "https://us.i.posthog.com/";
                  # use variable to force proper DNS re-resolution, also must manually pass along path
                  proxy_pass $posthog_main$1$is_args$args;
                  proxy_set_header Host "us.i.posthog.com";
                '';
              };
            };

            "${domain}" = {
              enableACME     = true;
              forceSSL       = true;
              globalRedirect = "www.${domain}";
            };

            "www.${domain}" = {
              enableACME = true;
              forceSSL   = true;

              locations."~ ^(.+\\.php)(.*)$" = {
                extraConfig = "rewrite ^ /tarpit break;";
                proxyPass   = "http://127.0.0.1:8080";
              };

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
        uberjar  = "com.popaithesailor.www-unversioned-standalone.jar";
        localBin = "/usr/local/bin";
        path     = "${localBin}/${uberjar}";
        nextPath = "/tmp/${uberjar}";
        stateDir = "www";
        jreFlags = "-XX:MaxRAMPercentage=75";
      in {
        services.www = {
          requiredBy = [ "multi-user.target" ];

          serviceConfig = {
            Environment = builtins.concatStringsSep " " [
              "NEXT_PATH=${nextPath}"
              "DB_STORAGE=/var/lib/${stateDir}/db"
              "POSTHOG=true"
              "PRETTY_PRINT=true"
            ];

            User                = config.users.users.www.name;
            Group               = config.users.users.www.group;
            SupplementaryGroups = config.services.nginx.group;

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
            ReadOnlyPaths         = "/var/log/nginx/access.log";
            ReadWritePaths        = localBin;
            RestrictRealtime      = true;
            RestrictSUIDSGID      = true;
            StateDirectory        = stateDir;
            SystemCallFilter      = [ "@system-service" ];

            Restart           = "always";
            SuccessExitStatus = 143;

            ExecStart    = "${pkgs.temurin-jre-bin}/bin/java ${jreFlags} -jar ${path}";
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
