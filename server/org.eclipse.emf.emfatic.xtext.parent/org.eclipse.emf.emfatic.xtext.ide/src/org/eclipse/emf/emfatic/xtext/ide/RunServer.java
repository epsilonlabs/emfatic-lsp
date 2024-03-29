package org.eclipse.emf.emfatic.xtext.ide;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.Channels;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

import org.eclipse.lsp4j.jsonrpc.JsonRpcException;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.jsonrpc.MessageConsumer;
import org.eclipse.lsp4j.jsonrpc.MessageIssueException;
import org.eclipse.lsp4j.jsonrpc.messages.Message;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.xtext.ide.server.LanguageServerImpl;
import org.eclipse.xtext.ide.server.ServerModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Source:
 * https://github.com/itemis/xtext-languageserver-example/blob/master/org.xtext.example.mydsl.ide/src/org/xtext/example/mydsl/ide/RunServer.java
 * 
 * @author dietrich - Initial contribution and API
 * @author alfonsodelavega - Some tweaks
 */
public class RunServer {

	public static boolean debug = true;

	static void debug(String message) {
		if (debug) {
			System.out.println("Server: " + message);
		}
	}

	public static void main(String[] args) throws InterruptedException, IOException {
		Injector injector = Guice.createInjector(new ServerModule());
		LanguageServerImpl languageServer = injector.getInstance(LanguageServerImpl.class);
		Function<MessageConsumer, MessageConsumer> wrapper = consumer -> {
			MessageConsumer result = new MessageConsumer() {

				@Override
				public void consume(Message message) throws MessageIssueException, JsonRpcException {
					System.out.println(message);
					consumer.consume(message);

				}
			};
			return result;
		};

		debug("Establishing socket and waiting for connection");

		Launcher<LanguageClient> launcher = createSocketLauncher(languageServer,
				LanguageClient.class, new InetSocketAddress("localhost", 5007),
				Executors.newCachedThreadPool(), wrapper);

		debug("Connection established");

		languageServer.connect(launcher.getRemoteProxy());
		Future<?> future = launcher.startListening();

		while (!future.isDone()) {
			debug("Active");
			Thread.sleep(30_000l);
		}
		debug("Shut down");
	}

	static <T> Launcher<T> createSocketLauncher(Object localService, Class<T> remoteInterface, SocketAddress socketAddress,
			ExecutorService executorService, Function<MessageConsumer, MessageConsumer> wrapper) throws IOException {

		try (AsynchronousServerSocketChannel serverSocket = AsynchronousServerSocketChannel.open().bind(socketAddress)) {
			AsynchronousSocketChannel socketChannel = serverSocket.accept().get();
			return Launcher.createIoLauncher(localService, remoteInterface, Channels.newInputStream(socketChannel),
					Channels.newOutputStream(socketChannel), executorService, wrapper);
		}
		catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}

}