#!/usr/bin/env bash

set -euo pipefail

# Script to download a pre-trained word vector model.
#   Configurable Options:
#     Output Directory (defaults to current directory)
#     File to download (defaults to glove.6B.zip)
#   See https://nlp.stanford.edu/projects/glove/ for details of these models

# Constants
readonly SCRIPT_NAME="getModelBinary.sh"
readonly DEFAULT_ZIP_NAME="glove.6B.zip";
readonly DEFAULT_OUTPUT_DIRECTORY=$(pwd)

# Variables
zip_name=$DEFAULT_ZIP_NAME
output_directory=$DEFAULT_OUTPUT_DIRECTORY

print_help () {
  echo "Usage: ./$SCRIPT_NAME [options]"
  echo ""
  echo "Options:"
  echo "  -f   ZIP file to download. Default: $DEFAULT_ZIP_NAME"
  echo "  -o   Download output directory. Default: $DEFAULT_OUTPUT_DIRECTORY"
  echo "  -h   Display this menu"
  echo ""
  echo "For example models to download, see https://nlp.stanford.edu/projects/glove/"
}

process_cli_options () {

  local OPTIND

  while getopts 'h,f:,:o:' flag; do
    case "${flag}" in
      h)
        print_help
        exit
        ;;
      f)
        zip_name=${OPTARG}
        ;;
      o)
        output_directory=${OPTARG}
        ;;
      *)
        echo "Unexpected option, exiting"
        exit
        ;;
    esac
  done

}

remove_trailing_slash_from_output_directory_if_exists () {
  if [[ "$output_directory" =~ /$ ]]; then
    output_directory=${output_directory%?}
  fi
}

download_zip () {
  if [[ -f "$output_directory/$zip_name" ]]; then
    echo "$output_directory/$zip_name already exists. Skipping download";
  else
    echo "Downloading $zip_name"
    local download_url="http://nlp.stanford.edu/data/$zip_name"
    wget "$download_url" -O "$output_directory/$zip_name"
  fi
}

main () {
  process_cli_options "$@"
  remove_trailing_slash_from_output_directory_if_exists
  download_zip
  # If unzipped files already exist, user will be prompted to overwrite
  unzip "$output_directory/$zip_name"
}

main "$@"
