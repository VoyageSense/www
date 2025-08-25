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

{ }:

let
  nixpkgs = builtins.fetchTarball {
    url = "https://github.com/NixOS/nixpkgs/archive/80b53fdb4f883238807ced86db41be809d60f3b5.tar.gz";
    sha256 = "sha256:03kvxx993dx7d9933ix78ssi6wz33hrhzkl2aiq32fx3isnp5syi";
  };

  pkgs = import nixpkgs { };

  config = {
    imports = ["${nixpkgs}/nixos/modules/virtualisation/digital-ocean-image.nix" ];

    services.getty.helpLine = builtins.concatStringsSep "\n"
      (builtins.map (interface: "\\4{${interface}} \\6{${interface}}")
        [ "ens3" "ens4" ]);

    system.stateVersion = "24.11";

    virtualisation.digitalOceanImage.compressionMethod = "bzip2";
  };
in
(pkgs.nixos config).digitalOceanImage
