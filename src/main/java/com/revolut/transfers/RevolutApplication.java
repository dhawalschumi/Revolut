package com.revolut.transfers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.revolut.account.service.AccountService;
import com.revolut.account.service.AccountServiceImpl;
import com.revolut.balance.service.BalanceService;
import com.revolut.balance.service.BalanceServiceImpl;
import com.revolut.handlers.AccountDetailsHandler;
import com.revolut.handlers.TransfersHandler;
import com.revolut.query.service.QueryService;
import com.revolut.query.service.QueryServiceImpl;
import com.revolut.transfers.database.DatabaseManager;
import com.revolut.transfers.database.DatabaseManagerImpl;
import com.revolut.transfers.service.TransferService;
import com.revolut.transfers.service.TransferServiceImpl;

import ratpack.func.Action;
import ratpack.server.RatpackServer;
import ratpack.server.RatpackServerSpec;

/**
 * @author Dhawal Patel
 * 
 */

public class RevolutApplication {

	public static void main(String[] args) {
		Action<RatpackServerSpec> serverSpec = (server) -> {
			server.serverConfig(builder -> {
				builder.development(false);
				builder.threads(200);
				builder.registerShutdownHook(true);
				builder.build();
			});
			server.registryOf(registry -> {
				DatabaseManager databaseManager = new DatabaseManagerImpl();
				QueryService queryService = new QueryServiceImpl(databaseManager);
				BalanceService balanceService = new BalanceServiceImpl(queryService);
				AccountService accountService = new AccountServiceImpl(queryService);
				ObjectMapper mapper = new ObjectMapper().disable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
						.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN)
						.setNodeFactory(JsonNodeFactory.withExactBigDecimals(true));
				registry.add(TransferService.class, new TransferServiceImpl(balanceService, accountService));
				registry.add(ObjectMapper.class, mapper);
				registry.add(TransfersHandler.class, new TransfersHandler());
				registry.add(AccountDetailsHandler.class, new AccountDetailsHandler(accountService));
				registry.add(DatabaseManager.class, databaseManager);
			}).handlers(chain -> chain.post("transfer", TransfersHandler.class).post("accountDetails",
					AccountDetailsHandler.class));
		};
		try {
			RatpackServer server = RatpackServer.start(serverSpec);
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				try {
					server.getRegistry().get().get(DatabaseManager.class).shutDownDB();
				} catch (Exception e) {
					e.printStackTrace(System.out);
				}
			}));
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
}