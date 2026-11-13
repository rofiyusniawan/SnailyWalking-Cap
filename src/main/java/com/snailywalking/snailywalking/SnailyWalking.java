package com.snailywalking.snailywalking;

import javax.swing.SwingUtilities;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SnailyWalking {

    public static void main(String[] args) {

        ApplicationContext context =
                new ClassPathXmlApplicationContext("appcontext.xml");

        SwingUtilities.invokeLater(() -> {
            LoginFrame login = new LoginFrame(context);
            login.setVisible(true);
        });
    }
}