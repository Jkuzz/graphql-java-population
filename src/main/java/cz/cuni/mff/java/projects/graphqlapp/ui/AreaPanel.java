package cz.cuni.mff.java.projects.graphqlapp.ui;

import graphql.GraphQL;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;

/**
 * Main panel of the area selection and search functionality.
 */
public class AreaPanel extends JPanel {
    /**
     * Used by layout manager to layout child components.
     */
    private final GridBagConstraints gbc = new GridBagConstraints();
    /**
     * Listener for the Add Area button - handles adding new PopulationCards
     */
    private AreaAddListener areaAddListener;
    /**
     * Filter used for filtering the currently-selected areas list pre-render.
     */
    private String searchFilter = "";
    /**
     * JList displayed by the JScrollPane containing addable AreaListItems
     */
    private JList<AreaListItem> areaList;
    /**
     * Holds members of the three types of areas. Contains all areas which are not currently
     * being displayed as PopulationCards. Displaying in JList is done by filtering
     * this list with search query.
     */
    private final AreaListManager areaListManager;
    /**
     * Background Colour of the panel.
     */
    private final Color bgColor;
    /**
     * Which area type list is currently being displayed. Used for getting the list from the AreaListManager.
     * Toggled by area change buttons.
     */
    private AreaType currentAreaType = AreaType.KRAJE;


    /**
     * @return JList component that displays available areas.
     */
    public JList<AreaListItem> getAreaList() {
        return areaList;
    }


    /**
     * Gives access to the listener of the Add Area button, to set target JPanel once it's created
     * @return the add area listener
     */
    public AreaAddListener getAddAreaListener() {
        return areaAddListener;
    }


    /**
     * Create the panel.
     * @param graphQL database provider instance
     * @param bgCol background colour of the panel
     */
    public AreaPanel(GraphQL graphQL, Color bgCol) {
        areaListManager = new AreaListManager(graphQL);
        bgColor = bgCol;
        this.setLayout(new GridBagLayout());
        makeAreaPanel();
    }


    /**
     * Creates a panel that provides the list of available areas to query,
     * and an option to add them to the display panel.
     *
     * !Must set AddAreaListener target panel before use!
     */
    private void makeAreaPanel() {
        gbc.anchor = GridBagConstraints.PAGE_START;
        gbc.insets = new Insets(3, 20, 3, 20);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        this.add(makeAreaButtons(), gbc);

        JTextField areaNameSearch = new JTextField();
        gbc.gridy = GridBagConstraints.RELATIVE;
        this.add(areaNameSearch, gbc);

        gbc.weighty = 1;
        this.add(makeAreasScrollPane(), gbc);

        areaNameSearch.getDocument().addDocumentListener(new SearchChangeListener(this));

        gbc.weighty = 0;
        gbc.insets.bottom = 10;
        this.add(makeAreaBottomButtons(), gbc);

        this.setBackground(bgColor);
    }


    /**
     * Creates a panel containing the area type (codebook) switcher buttons
     * @return Panel with the buttons
     */
    private JPanel makeAreaButtons() {
        JPanel btnPanel = new JPanel();
        JButton krajeButton = new JButton("Kraje");
        JButton okresyButton = new JButton("Okresy");
        JButton obceButton = new JButton("Obce");
        krajeButton.addActionListener(actionEvent -> {
            currentAreaType = AreaType.KRAJE;
            updateListModel();
        });
        okresyButton.addActionListener(actionEvent -> {
            currentAreaType = AreaType.OKRESY;
            updateListModel();
        });
        obceButton.addActionListener(actionEvent -> {
            currentAreaType = AreaType.OBCE;
            updateListModel();
        });
        btnPanel.add(krajeButton);
        btnPanel.add(okresyButton);
        btnPanel.add(obceButton);

        btnPanel.setBackground(bgColor);
        return btnPanel;
    }


    /**
     * Creates a panel containing the 'add area' button
     * @return Panel containing the button
     */
    private JPanel makeAreaBottomButtons() {
        JPanel btnPanel = new JPanel();
        JButton addButton = new JButton("Add");
        addButton.addActionListener(areaAddListener);
        btnPanel.add(addButton);
        btnPanel.setBackground(bgColor);
        return btnPanel;
    }


    /**
     * Creates a JScrollPane containing a list of available areas to display
     * @return the scroll pane
     */
    private JScrollPane makeAreasScrollPane() {
        areaList = new JList<>();
        setSearchFilter("");

        areaAddListener = new AreaAddListener(this);
        return new JScrollPane(areaList);
    }


    /**
     * Iterates the currently selected area type's AreaListItems, sorts them alphabetically,
     * filters them according to searchFilter and updates the area list model to display them.
     */
    void updateListModel() {
        ArrayList<AreaListItem> listToDisplay = areaListManager.getListByType(currentAreaType);
        listToDisplay.sort(Comparator.comparing(AreaListItem::name));

        DefaultListModel<AreaListItem> processedModel = new DefaultListModel<>();
        for(AreaListItem item: listToDisplay) {
            if(item.name().toLowerCase(Locale.ROOT).contains(searchFilter.toLowerCase(Locale.ROOT))) {
                processedModel.addElement(item);
            }
        }
        areaList.setModel(processedModel);
    }

    /**
     * Change the search filter and update view
     * @param filter new filter
     */
    void setSearchFilter(String filter) {
        this.searchFilter = filter;
        updateListModel();
    }

    /**
     * Remove list item from base items and update view
     * @param item to remove
     */
    void removeItem(AreaListItem item) {
        areaListManager.remove(item);
        updateListModel();
    }

    /**
     * Add list item to base items and update view
     * @param item to add
     */
    void addItem(AreaListItem item) {
        areaListManager.add(item);
        updateListModel();
    }

}
