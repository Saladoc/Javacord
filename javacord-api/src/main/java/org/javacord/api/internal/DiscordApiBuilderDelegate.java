package org.javacord.api.internal;

import org.javacord.api.AccountType;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.listener.GloballyAttachableListener;
import org.javacord.api.util.auth.Authenticator;

import java.net.Proxy;
import java.net.ProxySelector;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * This class is internally used by the {@link DiscordApiBuilder} to create discord api instances.
 * You usually don't want to interact with this object.
 */
public interface DiscordApiBuilderDelegate {

    /**
     * Sets the proxy selector which should be used to determine the proxies that should be used to connect to the
     * Discord REST API and websocket.
     *
     * @param proxySelector The proxy selector to set.
     */
    void setProxySelector(ProxySelector proxySelector);

    /**
     * Sets the proxy which should be used to connect to the Discord REST API and websocket.
     *
     * @param proxy The poxy to set.
     */
    void setProxy(Proxy proxy);

    /**
     * Sets the authenticator that should be used to authenticate against proxies that require it.
     *
     * @param authenticator The proxy authenticator to set.
     */
    void setProxyAuthenticator(Authenticator authenticator);

    /**
     * Sets whether all SSL certificates should be trusted when connecting to the Discord API and websocket.
     *
     * @param trustAllCertificates Whether to trust all SSL certificates.
     */
    void setTrustAllCertificates(boolean trustAllCertificates);

    /**
     * Sets the token.
     *
     * @param token The token to set.
     */
    void setToken(String token);

    /**
     * Gets the token.
     *
     * @return The token.
     */
    Optional<String> getToken();

    /**
     * Sets the account type.
     *
     * @param accountType The account type to set.
     */
    void setAccountType(AccountType accountType);

    /**
     * Gets the account type.
     *
     * @return The account type.
     */
    AccountType getAccountType();

    /**
     * Sets the total shards.
     *
     * @param totalShards The total shards to set.
     * @see DiscordApiBuilder#setTotalShards(int)
     */
    void setTotalShards(int totalShards);

    /**
     * Gets the total shards.
     *
     * @return The total shards.
     */
    int getTotalShards();

    /**
     * Sets the current shards.
     *
     * @param currentShard The current shards to set.
     * @see DiscordApiBuilder#setCurrentShard(int)
     */
    void setCurrentShard(int currentShard);

    /**
     * Sets the current shard.
     *
     * @return The current shard.
     */
    int getCurrentShard();

    /**
     * Sets the wait for servers on startup flag.
     *
     * @param waitForServersOnStartup The wait for servers on startup flag to set.
     */
    void setWaitForServersOnStartup(boolean waitForServersOnStartup);

    /**
     * Checks if Javacord should wait for all servers to become available on startup.
     *
     * @return If Javacord should wait.
     */
    boolean isWaitingForServersOnStartup();

    /**
     * Logs the bot in.
     *
     * @return The discord api instance.
     */
    CompletableFuture<DiscordApi> login();

    /**
     * Login given shards to the account with the given token.
     * It is invalid to call {@link #setCurrentShard(int)} with
     * anything but {@code 0} before calling this method.
     *
     * @param shards The shards to connect, starting with {@code 0}!
     * @return A collection of {@link CompletableFuture}s which contain the {@code DiscordApi}s for the shards.
     */
    Collection<CompletableFuture<DiscordApi>> loginShards(int... shards);

    /**
     * Sets the recommended total shards.
     *
     * @return A future to check if the action was successful.
     */
    CompletableFuture<Void> setRecommendedTotalShards();

    /**
     * Register an event listener early.
     *
     * @param <T> The type oft he listener.
     * @param listenerClass The type of the listener.
     * @param listener The listener.
     */
    <T extends GloballyAttachableListener> void addListenerFor(Class<T> listenerClass, T listener);
}
