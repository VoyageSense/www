{ }:

let
  nixpkgs = builtins.fetchTarball {
    url = "https://github.com/NixOS/nixpkgs/archive/80b53fdb4f883238807ced86db41be809d60f3b5.tar.gz";
    sha256 = "sha256:03kvxx993dx7d9933ix78ssi6wz33hrhzkl2aiq32fx3isnp5syi";
  };

  pkgs = import nixpkgs { };

  config = {
    imports = ["${nixpkgs}/nixos/modules/virtualisation/digital-ocean-image.nix" ];

    system.stateVersion = "24.11";

    virtualisation.digitalOceanImage.compressionMethod = "bzip2";
  };
in
(pkgs.nixos config).digitalOceanImage
