package io.openems.edge.ess.generic.common.offgrid.statemachine;

import java.time.Instant;

import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.edge.common.startstop.StartStop;
import io.openems.edge.common.statemachine.StateHandler;
import io.openems.edge.common.sum.GridMode;
import io.openems.edge.ess.generic.common.GenericManagedEss;
import io.openems.edge.ess.generic.common.offgrid.statemachine.OffGridStateMachine.OffGridState;

public class StartedInOnGridHandler extends StateHandler<OffGridState, OffGridContext> {
	private Instant lastAttempt = Instant.MIN;

	@Override
	protected void onEntry(OffGridContext context) throws OpenemsNamedException {

		this.lastAttempt = Instant.now();
		System.out.println(this.lastAttempt);
	}

	@Override
	public OffGridState runAndGetNextState(OffGridContext context) throws OpenemsNamedException {
		GenericManagedEss ess = context.getParent();

		if (ess.hasFaults()) {
			return OffGridState.UNDEFINED;
		}

		if (!context.battery.isStarted()) {
			return OffGridState.UNDEFINED;
		}

		if (!context.batteryInverter.isStarted()) {
			return OffGridState.UNDEFINED;
		}

		// Grid is Off
		if (context.offGridSwitch.getGridStatus()) {
			context.batteryInverter.setOngridCommand(false);
			return OffGridState.STOP_BATTERY_INVERTER_BEFORE_SWITCH;
		}
		context.batteryInverter.setOngridCommand(true);
		context.getParent()._setGridMode(GridMode.ON_GRID);
		ess._setStartStop(StartStop.START);
		return OffGridState.STARTED_IN_ON_GRID;

	}
}
