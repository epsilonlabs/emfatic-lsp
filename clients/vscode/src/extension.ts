'use strict';

import * as net from 'net';

import { Trace } from 'vscode-jsonrpc';
import { workspace, ExtensionContext } from 'vscode';
import { LanguageClient, LanguageClientOptions, StreamInfo,
    Position as LSPosition,
    Location as LSLocation } from 'vscode-languageclient/node';

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

    // Create the language client and start the client.
    client = new LanguageClient('emfaticlsp', 'Emfatic Editor', serverOptions, clientOptions);

    client.start();

    // TODO: errors to investigate:
    // lc.trace = Trace.Verbose;
    // let disposable = lc.start();

    // Push the disposable to the context's subscriptions so that the
    // client can be deactivated on extension deactivation
    // context.subscriptions.push(disposable);
}

export function deactivate(): Thenable<void> | undefined {
    if (!client) {
      return undefined;
    }
    return client.stop();
  }
