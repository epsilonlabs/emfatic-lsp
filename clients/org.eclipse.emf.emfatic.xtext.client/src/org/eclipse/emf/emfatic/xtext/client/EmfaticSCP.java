package org.eclipse.emf.emfatic.xtext.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.lsp4e.server.StreamConnectionProvider;

public class EmfaticSCP implements StreamConnectionProvider {

	private Socket serverSocket;

	@Override
	public void start() throws IOException {
		serverSocket = new Socket("localhost", 5007);
	}

	@Override
	public InputStream getInputStream() {
		if(this.serverSocket != null) {
			try {
				return this.serverSocket.getInputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public OutputStream getOutputStream() {
		if(this.serverSocket != null) {
			try {
				return this.serverSocket.getOutputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public @Nullable InputStream getErrorStream() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void stop() {
		if(this.serverSocket != null) {
			try {
				this.serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
