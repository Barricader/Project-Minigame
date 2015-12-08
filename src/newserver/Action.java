package newserver;


/**
 * This is a functional interface for providing an easy way to execute
 * methods, using lambda expressions. A simple () -> methodCall() syntax,
 * allows for a clean way of passing method functionality where an action is
 * needed to execute a command. This is explicitly used in the ServerIOHandler,
 * where Action is the value stored in a HashMap, and based on the String command
 * key, we can pass in the method needed to execute the action, from said command.
 * @author David Kramer
 *
 */
@FunctionalInterface
public interface Action {
	public void execute();
}
