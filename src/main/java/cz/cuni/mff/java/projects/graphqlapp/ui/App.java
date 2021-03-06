package cz.cuni.mff.java.projects.graphqlapp.ui;

import cz.cuni.mff.java.projects.graphqlapp.provider.GraphQLProvider;
import graphql.GraphQL;

import javax.swing.*;
import java.awt.*;

/**
 * GUI application for displaying per-area demographics.
 */
public class App {

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Population View");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(createContent());

        frame.setMinimumSize(new Dimension(1200, 750));
        frame.pack();
        frame.setVisible(true);
    }

    private static JPanel createContent() {
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        contentPanel.setBackground(new Color(60, 60, 60));
        GraphQL graphQL = new GraphQLProvider().getGraphQL();

        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(15, 20, 15, 10);
        gbc.ipadx = 20;
        gbc.ipady = 20;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.weighty = 1;

        AreaPanel areaPanel = new AreaPanel(graphQL, new Color(40, 40, 150));
        contentPanel.add(areaPanel, gbc);

        gbc.gridx = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(15, 10, 15, 20);
        gbc.weightx = 1;
        PopulationPanel popPanel = new PopulationPanel(areaPanel, graphQL);
        contentPanel.add(popPanel, gbc);

        areaPanel.getAddAreaListener().setPopulationPanel(popPanel);
        return contentPanel;
    }

    /**
     * GUI App entrypoint
     * @param args program arguments - unused
     */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(App::createAndShowGUI);
    }
}
