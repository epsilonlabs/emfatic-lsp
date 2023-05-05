# Requirements

Needs LSP4E version 0.21.0. You can add a new repository and point it to this updatesite: `https://download.eclipse.org/lsp4e/releases/0.21.0`.

I am using Eclipse 2022-09. With newer versions you might get an incompatibility issues with some of the dependencies required by lsp4e.

# Testing


- Start the Emfatic LSP server from ´org.eclipse.emf.emfatic.xtext.ide´
- Run the client plugin in a nested eclipse
- Open/create an `*.lspemf` file.

You should see validation and content assist working. 

**Note:** If you close the editor, the server will stop. This could be fixed by changing the `EmfaticSCP#start()` to lunch the server.