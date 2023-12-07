# VSCode LSP client for Emfatic

## How to run

1. Open the `vscode` folder in Visual Studio code
2. To build the typescript sources (requires [tsc compiler](https://code.visualstudio.com/docs/typescript/typescript-compiling)), `F1` -> `Tasks: Run Build Task`. An `out` folder should be generated.
3. From Eclipse, run `org.eclipse.emf.emfatic.xtext.ide/src/org/eclipse/emf/emfatic/xtext/ide/RunServer.java`
4. Again from VSCode, press `F5` to run a new VSCode instance with the extension.
5. Create/Open an `.lspemf` file.
6. Profit!

## What works

- Syntax highlighting
- Validation

## What does not work

- Content assist (only words appearing in the file are suggested, without any contextual meaning)
