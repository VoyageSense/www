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

{ config, pkgs, ... }:

{
  imports = [
    <nixpkgs/nixos/modules/minimal.nix>
    <nixpkgs/nixos/modules/headless.nix>
    <nixpkgs/nixos/modules/perlless.nix>
    <nixpkgs/nixos/modules/virtualisation/digital-ocean-config.nix>
  ];

  networking = {
    hostName = "www";
    domain   = "popaithesailor.com";
  };

  services.nginx.enable = true;

  systemd.network.enable = true;
}
