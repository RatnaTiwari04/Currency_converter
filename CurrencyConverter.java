import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.sql.*;
import java.util.*;

public class CurrencyConverter extends JFrame {

    private JComboBox<String> fromCurrencyDropdown;
    private JComboBox<String> toCurrencyDropdown;
    private JTextField amountField;
    private JLabel resultLabel;
    private ArrayList<String> currencies;
    private BufferedImage backgroundImage;

    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/CurrencyDB";
    private static final String DB_USER = "root";  
    private static final String DB_PASSWORD = "Password";  

    public CurrencyConverter() {
        
        currencies = initializeCurrencies();

       
        try {
            backgroundImage = ImageIO.read(new File("background.jpg"));
        } catch (IOException e) {
            System.out.println("Error loading background image: " + e.getMessage());
        }

        
        setTitle("Currency Converter");
        setSize(500, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
                }
            }
        };
        panel.setBackground(new Color(45, 52, 54)); 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;

       
        Font labelFont = new Font("Arial", Font.BOLD, 18);
        Font resultFont = new Font("Arial", Font.BOLD, 20);
        Color labelColor = Color.WHITE;
        Color fieldColor = Color.LIGHT_GRAY;
        Color resultColor = new Color(39, 174, 96); // Green for results

        // Amount input label and field
        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setFont(labelFont);
        amountLabel.setForeground(labelColor);
        panel.add(amountLabel, gbc);
        gbc.gridx = 1;
        amountField = new JTextField(12);
        amountField.setBackground(fieldColor);
        panel.add(amountField, gbc);

        // From currency dropdown
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel fromLabel = new JLabel("From Currency:");
        fromLabel.setFont(labelFont);
        fromLabel.setForeground(labelColor);
        panel.add(fromLabel, gbc);
        gbc.gridx = 1;
        fromCurrencyDropdown = new JComboBox<>();
        populateCurrencyDropdown(fromCurrencyDropdown);
        fromCurrencyDropdown.setBackground(fieldColor);
        panel.add(fromCurrencyDropdown, gbc);

        // To currency dropdown
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel toLabel = new JLabel("To Currency:");
        toLabel.setFont(labelFont);
        toLabel.setForeground(labelColor);
        panel.add(toLabel, gbc);
        gbc.gridx = 1;
        toCurrencyDropdown = new JComboBox<>();
        populateCurrencyDropdown(toCurrencyDropdown);
        toCurrencyDropdown.setBackground(fieldColor);
        panel.add(toCurrencyDropdown, gbc);

        // Convert button
        gbc.gridx = 1;
        gbc.gridy = 3;
        JButton convertButton = new JButton("Convert");
        convertButton.setBackground(new Color(0, 123, 255)); // Blue button
        convertButton.setForeground(Color.WHITE);
        convertButton.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(convertButton, gbc);

       
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        resultLabel = new JLabel("Converted amount will appear here.");
        resultLabel.setFont(resultFont);
        resultLabel.setForeground(resultColor);
        panel.add(resultLabel, gbc);

        
        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                convertCurrency();
            }
        });

        
        add(panel);
    }

    
    private void populateCurrencyDropdown(JComboBox<String> dropdown) {
        for (String currency : currencies) {
            dropdown.addItem(currency);
        }
    }

   
    private ArrayList<String> initializeCurrencies() {
        ArrayList<String> currencyList = new ArrayList<>();
        currencyList.add("USD");
        currencyList.add("EUR");
        currencyList.add("GBP");
        currencyList.add("INR");
        currencyList.add("CAD");
        currencyList.add("AUD");
        currencyList.add("JPY");
       
        return currencyList;
    }

    private void convertCurrency() {
        String fromCurrencyCode = (String) fromCurrencyDropdown.getSelectedItem();
        String toCurrencyCode = (String) toCurrencyDropdown.getSelectedItem();
        double amount;

        try {
            amount = Double.parseDouble(amountField.getText());
        } catch (NumberFormatException e) {
            resultLabel.setText("Please enter a valid number for the amount.");
            return;
        }

        // Fetch exchange rate from the database
        Double exchangeRate = getExchangeRate(fromCurrencyCode, toCurrencyCode);
        if (exchangeRate != null) {
            double convertedAmount = Math.round(amount * exchangeRate * 100.0) / 100.0;
            resultLabel.setText("Converted amount: " + convertedAmount + " " + toCurrencyCode);
        } else {
            resultLabel.setText("No exchange rate found between the selected currencies.");
        }
    }

    
private Double getExchangeRate(String fromCurrency, String toCurrency) {
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
        conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        String query = "SELECT Rate FROM exchangerates WHERE fromCurrency = ? AND toCurrency = ?";
        pstmt = conn.prepareStatement(query);
        pstmt.setString(1, fromCurrency);
        pstmt.setString(2, toCurrency);
        rs = pstmt.executeQuery();

        if (rs.next()) {
            return rs.getDouble("Rate");
        }

    } catch (SQLException e) {
        System.out.println("Database error: " + e.getMessage());
    } finally {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.out.println("Error closing resources: " + e.getMessage());
        }
    }

    return null;
}


    // Main method to run the application
    public static void main(String[] args) {
        // Load MySQL JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL Driver not found: " + e.getMessage());
        }

        // Start the application
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CurrencyConverter().setVisible(true);
            }
        });
    }
}