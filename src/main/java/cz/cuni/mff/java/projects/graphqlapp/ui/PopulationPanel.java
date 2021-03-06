package cz.cuni.mff.java.projects.graphqlapp.ui;

import graphql.GraphQL;
import graphql.com.google.common.collect.ImmutableMap;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLSchemaElement;
import graphql.schema.GraphQLType;

import java.util.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * JPanel containing PopulationCards and field selection checkbox
 */
public class PopulationPanel extends JPanel {
    /**
     * Associated AreaPanel. Adds areas to this Panel. Upon Card removal, return them to the areaPanel.
     */
    private final AreaPanel areaPanel;
    /**
     * GraphQL Database instance to query
     */
    private final GraphQL graphQL;
    /**
     * Background colour of the panel
     */
    private final Color BG_COLOR = new Color(100, 100, 200);
    /**
     * Background colours of PopulationCards by AreaType. Provided to Cards in constructor.
     */
    private final Map<AreaType, Color> PopCardColors = ImmutableMap.of(
            AreaType.KRAJE, new Color(130, 130, 190),
            AreaType.OKRESY, new Color(150, 100, 200),
            AreaType.OBCE, new Color(200, 100, 200)
    );
    /**
     * Map of field name: true if checked
     */
    private final HashMap<String, Boolean> selectedFields = new HashMap<>();
    /**
     * List of field checkboxes. Updates selectedFields, this is more annoying to get from.
     */
    private final ArrayList<JCheckBox> fieldCheckBoxes = new ArrayList<>();
    /**
     * List of all currently displayed PopulationCards
     */
    private final ArrayList<PopulationCard> populationCards = new ArrayList<>();
    /**
     * Used by layout manager to layout child components.
     */
    private final GridBagConstraints gbc = new GridBagConstraints();

    /**
     * Constructs the PopulationPanel and makes it ready to accept new AreaCards.
     * @param areaPanel associated AreaPanel which add areas
     * @param graphQL database instance to query
     */
    public PopulationPanel(AreaPanel areaPanel, GraphQL graphQL) {
        this.setLayout(new GridBagLayout());
        this.areaPanel = areaPanel;
        this.graphQL = graphQL;
        this.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        gbc.insets = new Insets(0, 5, 0, 5);
        gbc.anchor = GridBagConstraints.PAGE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        this.add(makeSelectPanel(), gbc);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(20, 5, 0, 5);
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.weighty = 1;
        this.setBackground(BG_COLOR);
    }

    /**
     * Creates the field selection panel that includes CheckBoxes for each demographic field that is
     * provided by the GraphQL database schema. Includes confirm button.
     *
     * @return field selection panel
     */
    private JPanel makeSelectPanel() {
        JPanel selectPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints(
                0, 0, 1, 1, 1, 0, GridBagConstraints.PAGE_START,
                GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0
        );
        selectPanel.add(new JLabel("Select fields to display"), gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weighty = 1;
        selectPanel.add(makeCheckboxPanel(), gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.PAGE_END;
        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;

        JButton fieldsSelectButton = new JButton("Select");
        fieldsSelectButton.addActionListener(__ -> updateSelectedFields());
        selectPanel.add(fieldsSelectButton, gbc);
        selectPanel.setBackground(BG_COLOR.brighter());
        return selectPanel;
    }

    /**
     * Create the part of the SelectPanel that includes a JCheckBox for each field in the schema.
     * @return panel with the JCheckBoxes
     */
    private JPanel makeCheckboxPanel() {
        GraphQLType demSchema = graphQL.getGraphQLSchema().getType("Demographics");
        assert demSchema != null;
        List<GraphQLSchemaElement> schemaElements = demSchema.getChildren();
        JPanel checkPanel = new JPanel(new GridLayout(schemaElements.size() / 3, 3));

        for(GraphQLSchemaElement element: schemaElements) {
            GraphQLFieldDefinition fieldDef = (GraphQLFieldDefinition) element;
            if(fieldDef.getName().equals("year")) {
                continue;  // Year is automatically queried for
            }
            JCheckBox fieldCheckBox = new JCheckBox(fieldDef.getName());
            selectedFields.put(fieldDef.getName(), false);
            fieldCheckBox.setOpaque(false);
            fieldCheckBoxes.add(fieldCheckBox);
            checkPanel.add(fieldCheckBox);
        }

        checkPanel.setBackground(BG_COLOR.brighter());
        return checkPanel;
    }

    /**
     * Create a PopulationCard for the provided AreaListItem and add it to the population view.
     * Queries database for population fields.
     * @param areaToAdd to add
     */
    public void addArea(AreaListItem areaToAdd) {
        assert areaPanel != null;
        PopulationCard popCard = new PopulationCard(
                areaToAdd, areaPanel, PopCardColors.get(areaToAdd.areaType()), selectedFieldsToArray(), graphQL
        );
        populationCards.add(popCard);

        gbc.gridx = GridBagConstraints.RELATIVE;
        this.add(popCard, gbc);
        this.revalidate();
    }

    /**
     * Remove the chosen PopulationCard from the panel and refresh
     * @param populationCard to remove
     */
    public void removeArea(PopulationCard populationCard) {
        this.remove(populationCard);
        populationCard.remove(populationCard);
        this.revalidate();
        this.getParent().repaint();
    }

    /**
     * Update all existing yearCards to show the demographic fields which are currently selected.
     */
    public void updateSelectedFields() {
        for(JCheckBox checkBox: fieldCheckBoxes) {
            selectedFields.put(checkBox.getText(), checkBox.isSelected());
        }
        for(PopulationCard populationCard: populationCards) {
            populationCard.updateYearDems(selectedFieldsToArray());
        }
    }

    /**
     * Transforms the selected fields map to array including those with true value.
     * @return selected fields
     */
    private ArrayList<String> selectedFieldsToArray() {
        ArrayList<String> selected = new ArrayList<>();
        for(Map.Entry<String, Boolean> e: selectedFields.entrySet()) {
            if(e.getValue()) {
                selected.add(e.getKey());
            }
        }
        return selected;
    }
}
