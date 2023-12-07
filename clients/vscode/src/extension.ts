'use strict';

import * as net from 'net';

import { Trace } from 'vscode-jsonrpc';
import { workspace, ExtensionContext } from 'vscode';
import { LanguageClient, LanguageClientOptions, StreamInfo} from 'vscode-languageclient/node';

let client: LanguageClient;

export function activate(context: ExtensionContext) {
    // The server is started as a separate app and listens on port 5007
    let connectionInfo = {
        port: 5007
    };
    let serverOptions = () => {
        // Connect to language server via socket
        let socket = net.connect(connectionInfo);
        let result: StreamInfo = {
            writer: socket,
            reader: socket
        };
        return Promise.resolve(result);
    };

    let clientOptions: LanguageClientOptions = {
        documentSelector: ['lspemf'],
        synchronize: {
            configurationSection: 'emfaticServer',
            fileEvents: workspace.createFileSystemWatcher('**/*.lspemf')
        },
    };

    client = new LanguageClient('emfaticlsp', 'Emfatic Editor', serverOptions, clientOptions);
    client.setTrace(Trace.Verbose);
    client.start();
}

export function deactivate(): Thenable<void> | undefined {
    if (!client) {
      return undefined;
    }
    return client.stop();
  }
