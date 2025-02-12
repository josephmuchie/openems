package io.openems.edge.common.test;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.osgi.service.component.ComponentContext;

import io.openems.common.OpenemsConstants;
import io.openems.common.exceptions.NotImplementedException;
import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.common.jsonrpc.base.JsonrpcRequest;
import io.openems.common.jsonrpc.base.JsonrpcResponseSuccess;
import io.openems.common.types.EdgeConfig;
import io.openems.edge.common.channel.Channel;
import io.openems.edge.common.component.ComponentManager;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.user.User;

/**
 * Simulates a ComponentManager for the OpenEMS Component test framework.
 */
public class DummyComponentManager implements ComponentManager {

	private final List<OpenemsComponent> components = new ArrayList<>();
	private final Clock clock;

	public DummyComponentManager() {
		this(Clock.systemDefaultZone());
	}

	public DummyComponentManager(Clock clock) {
		this.clock = clock;
	}

	@Override
	public List<OpenemsComponent> getEnabledComponents() {
		return Collections.unmodifiableList(this.components);
	}

	@Override
	public List<OpenemsComponent> getAllComponents() {
		return Collections.unmodifiableList(this.components);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends OpenemsComponent> List<T> getEnabledComponentsOfType(Class<T> clazz) {
		List<T> result = new ArrayList<>();
		for (OpenemsComponent component : this.components) {
			if (component.getClass().isInstance(clazz)) {
				result.add((T) component);
			}
		}
		return result;
	}

	/**
	 * Specific for this Dummy implementation.
	 * 
	 * @param component
	 */
	public DummyComponentManager addComponent(OpenemsComponent component) {
		if (component != this) {
			this.components.add(component);
		}
		return this;
	}

	@Override
	public EdgeConfig getEdgeConfig() {
		return new EdgeConfig();
	}

	@Override
	public String id() {
		return OpenemsConstants.COMPONENT_MANAGER_ID;
	}

	@Override
	public String alias() {
		return OpenemsConstants.COMPONENT_MANAGER_ID;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public ComponentContext getComponentContext() {
		return null;
	}

	@Deprecated()
	@Override
	public Channel<?> _channel(String channelName) {
		return null;
	}

	@Override
	public Collection<Channel<?>> channels() {
		return new ArrayList<>();
	}

	@Override
	public CompletableFuture<JsonrpcResponseSuccess> handleJsonrpcRequest(User user, JsonrpcRequest request)
			throws OpenemsNamedException {
		throw new NotImplementedException("handleJsonrpcRequest is not implemented for DummyComponentManager");
	}

	@Override
	public Clock getClock() {
		return this.clock;
	}

}