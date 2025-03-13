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
    domain   = "sailvisionpro.com";
  };

  services.nginx.enable = true;

  systemd.network.enable = true;
}
