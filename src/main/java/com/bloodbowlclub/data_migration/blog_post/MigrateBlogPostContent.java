package com.bloodbowlclub.data_migration.blog_post;


import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class MigrateBlogPostContent {
    @ShellMethod(key = "hello", value = "Affiche un message de salutation")
    public String hello(
            @ShellOption(defaultValue = "World") String name
    ) {
        return "Hello " + name + " 👋";
    }
}
