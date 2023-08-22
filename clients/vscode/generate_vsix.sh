#!/usr/bin/env bash

# Requires vsce:
# npm install @vscode/vsce

npm run build

vsce package -o out/
