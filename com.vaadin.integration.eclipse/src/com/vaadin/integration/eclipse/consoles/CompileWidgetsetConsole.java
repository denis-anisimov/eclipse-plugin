package com.vaadin.integration.eclipse.consoles;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;

public class CompileWidgetsetConsole extends AbstractVaadinConsole {

    private static final String WS_COMPILATION_CONSOLE_NAME = "Vaadin Widgetset Compilation";

    public CompileWidgetsetConsole() {
        super(WS_COMPILATION_CONSOLE_NAME);
    }

    public static CompileWidgetsetConsole get() {
        AbstractVaadinConsole console = findConsole(WS_COMPILATION_CONSOLE_NAME);

        if (console == null) {
            ConsolePlugin plugin = ConsolePlugin.getDefault();
            IConsoleManager conMan = plugin.getConsoleManager();

            // no console found, so create a new one
            console = new CompileWidgetsetConsole();
            conMan.addConsoles(new IConsole[] { console });
        }

        return (CompileWidgetsetConsole) console;
    }
}
