package com.revolut.transfers;

import com.revolut.handlers.TransfersHandler;
import com.revolut.transfers.database.DatabaseManager;
import com.revolut.transfers.database.DatabaseManagerImpl;

import ratpack.func.Action;
import ratpack.registry.NotInRegistryException;
import ratpack.server.RatpackServer;
import ratpack.server.RatpackServerSpec;

public class RevolutApplication {

	public static void main(String[] args) {
		Action<RatpackServerSpec> serverSpec = (server) -> {
			server.registryOf(registry -> {
				registry.add(DatabaseManager.class, new DatabaseManagerImpl());
				registry.add(TransfersHandler.class, new TransfersHandler());
			}).handlers(chain -> chain.post("transfer", TransfersHandler.class));
		};
		try {
			RatpackServer server = RatpackServer.start(serverSpec);
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				try {
					server.getRegistry().get().get(DatabaseManager.class).shutDownDB();
				} catch (NotInRegistryException e) {
					e.printStackTrace(System.out);
				} catch (Exception e) {
					e.printStackTrace(System.out);
				}	
			}));
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
}