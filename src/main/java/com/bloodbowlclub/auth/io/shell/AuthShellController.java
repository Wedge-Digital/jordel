package com.bloodbowlclub.auth.io.shell;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class AuthShellController {

    @ShellMethod(key = "hello-world", value = "Affiche une salutation personnalisée.")
    public String helloWorld(@ShellOption(defaultValue = "spring") String name) {
        return "Hello world " + name;
    }
}
