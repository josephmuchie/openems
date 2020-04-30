package io.openems.edge.common.statemachine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;

/**
 * Manages the States of the StateMachine.
 * 
 * @param <S> the {@link State} type, e.g. typically an enum
 * @param <C> the context type, i.e. a class wrapping a State-Machine context
 */
public class StateMachine<S extends State<S, C>, C> {

	private final Logger log = LoggerFactory.getLogger(StateMachine.class);

	private final S initialState;

	private S state;

	/**
	 * Initialize the State-Machine and set an initial State.
	 * 
	 * @param initialState the initial State
	 */
	public StateMachine(S initialState) {
		this.initialState = initialState;
		this.state = initialState;
	}

	/**
	 * Gets the currently activate State.
	 * 
	 * @return the State
	 */
	public S getCurrentState() {
		return this.state;
	}

	/**
	 * Forcibly change the next State from outside. Use with care!
	 * 
	 * <p>
	 * Note that transition events will get called.
	 * 
	 * @param state the next State
	 */
	public void forceNextState(S state) {
		this.forceNextState = state;
	}

	private S forceNextState = null;

	/**
	 * Execute the StateMachine.
	 * 
	 * @throws OpenemsNamedException on error
	 */
	public void run(C context) throws OpenemsNamedException {
		// Keep last State
		S lastState = this.state;

		OpenemsNamedException exception = null;

		// Evaluate the next State
		S nextState;
		if (this.forceNextState != null) {
			// Apply Force-Next-State
			nextState = this.forceNextState;
			this.forceNextState = null;

		} else {
			try {
				// Call the State Handler and receive next State.
				nextState = this.state.getHandler().getNextState(context);
			} catch (OpenemsNamedException e) {
				exception = e;
				nextState = this.initialState; // set to initial state on error
			}
		}

		// save next State
		this.state = nextState;

		// Call StateMachine events on transition
		if (lastState != this.state) {
			this.log.info("Changing StateMachine from [" + lastState + "] to [" + this.state + "]");

			// On-Exit of the last State
			try {
				lastState.getHandler().onExit(context);
			} catch (OpenemsNamedException e) {
				if (exception != null) {
					e.addSuppressed(exception);
				}
				exception = e;
			}

			// On-Entry of next State
			try {
				this.state.getHandler().onEntry(context);
			} catch (OpenemsNamedException e) {
				if (exception != null) {
					e.addSuppressed(exception);
				}
				exception = e;
			}
		}

		// Handle Exception
		if (exception != null) {
			throw exception;
		}
	}
}
