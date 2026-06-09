package animesuggestiongenerator;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import javax.imageio.ImageIO;

@SuppressWarnings("unused")
public class AppGui {

    private static final Color COLOR_BG         = new Color(15, 15, 25);
    private static final Color COLOR_PANEL      = new Color(25, 25, 40);
    private static final Color COLOR_ACCENT     = new Color(99, 179, 237);
    private static final Color COLOR_ACCENT_ALT = new Color(236, 100, 135);
    private static final Color COLOR_TEXT       = new Color(220, 220, 235);
    private static final Color COLOR_TEXT_DIM   = new Color(130, 130, 160);
    private static final Color COLOR_FIELD      = new Color(35, 35, 55);

    private final JFrame frame;
    private final CardLayout cardLayout;
    private final JPanel rootPanel;
    private JPanel cardContainer;
    private JScrollPane cardScroll;

    public AppGui() {
        frame = new JFrame("Anime Suggestion Generator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(580, 720);
        frame.setMinimumSize(new Dimension(480, 580));   
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);                        

        cardLayout = new CardLayout();
        rootPanel = new JPanel(cardLayout);
        rootPanel.setBackground(COLOR_BG);

        rootPanel.add(createWelcomeScreen(), "WELCOME");
        rootPanel.add(createLoginScreen(),   "LOGIN");
        rootPanel.add(createSignupScreen(),  "SIGNUP");
        rootPanel.add(createSearchScreen(),  "SEARCH");

        frame.setContentPane(rootPanel);
        frame.setVisible(true);
        cardLayout.show(rootPanel, "WELCOME");
    }

    private JPanel createWelcomeScreen() {
        JPanel panel = createBasePanel(new GridBagLayout());
        GridBagConstraints gbc = createGBC();

        JLabel logo       = createLabel("🎌", 56, Font.PLAIN, COLOR_TEXT);
        JLabel titleLine1 = createLabel("Anime Suggestion", 26, Font.BOLD, COLOR_TEXT);
        JLabel titleLine2 = createLabel("Generator", 26, Font.BOLD, COLOR_ACCENT);
        JLabel subtitle   = createLabel("Find your next favorite show.", 13, Font.PLAIN, COLOR_TEXT_DIM);

        JButton btnLogin  = createStyledButton("Login",   COLOR_ACCENT);
        JButton btnSignup = createStyledButton("Sign Up", COLOR_ACCENT_ALT);

        btnLogin.addActionListener(e -> cardLayout.show(rootPanel, "LOGIN"));
        btnSignup.addActionListener(e -> cardLayout.show(rootPanel, "SIGNUP"));

        addToPanel(panel, logo,       gbc, 0, 0, new Insets(0, 0, 10, 0));
        addToPanel(panel, titleLine1, gbc, 0, 1, new Insets(0, 0, 0,  0));
        addToPanel(panel, titleLine2, gbc, 0, 2, new Insets(0, 0, 8,  0));
        addToPanel(panel, subtitle,   gbc, 0, 3, new Insets(0, 0, 35, 0));
        addToPanel(panel, btnLogin,   gbc, 0, 4, new Insets(0, 0, 15, 0));
        addToPanel(panel, btnSignup,  gbc, 0, 5, new Insets(0, 0, 0,  0));

        return panel;
    }

    private JPanel createLoginScreen() {
        JPanel panel = createBasePanel(new GridBagLayout());
        GridBagConstraints gbc = createGBC();

        JTextField     userField  = createTextField("Username");
        JPasswordField passField  = createPasswordField("Password");
        JLabel         statusMsg  = createLabel("", 12, Font.PLAIN, COLOR_ACCENT_ALT);
        JButton        btnSubmit  = createStyledButton("Login", COLOR_ACCENT);
        JButton        btnBack    = createLinkButton("← Back to Home");

        btnSubmit.addActionListener(e -> {
            String user = userField.getText().trim();
            String pass = new String(passField.getPassword()).trim();

            if (user.isEmpty() || pass.isEmpty()) {
                statusMsg.setText("Please fill in both fields.");
                return;
            }

            if (UserManager.validateLogin(user, pass)) {
                clearLoginFields(userField, passField, statusMsg);
                cardLayout.show(rootPanel, "SEARCH");
            } else {
                statusMsg.setText("Wrong username or password. Try again.");
            }
        });

        btnBack.addActionListener(e -> {
            clearLoginFields(userField, passField, statusMsg);
            cardLayout.show(rootPanel, "WELCOME");
        });

        addToPanel(panel, createLabel("Welcome back!", 22, Font.BOLD, COLOR_TEXT), gbc, 0, 0, new Insets(0, 0, 30, 0));
        addToPanel(panel, makeLabeledField("Username",  userField),  gbc, 0, 1, new Insets(0, 0, 12, 0));
        addToPanel(panel, makeLabeledField("Password",  passField),  gbc, 0, 2, new Insets(0, 0, 5,  0));
        addToPanel(panel, statusMsg,  gbc, 0, 3, new Insets(0, 0, 20, 0));
        addToPanel(panel, btnSubmit,  gbc, 0, 4, new Insets(0, 0, 15, 0));
        addToPanel(panel, btnBack,    gbc, 0, 5, new Insets(0, 0, 0,  0));

        return panel;
    }

    private JPanel createSignupScreen() {
        JPanel panel = createBasePanel(new GridBagLayout());
        GridBagConstraints gbc = createGBC();

        JTextField     nameField    = createTextField("e.g. Md Zihad");
        JTextField     userField    = createTextField("e.g. xidu28");
        JTextField     emailField   = createTextField("e.g. zihad@gmail.com");
        JPasswordField passField    = createPasswordField("At least 8 characters");
        JPasswordField confirmField = createPasswordField("Re-enter your password");
        JLabel         statusMsg    = createLabel("", 12, Font.PLAIN, COLOR_ACCENT_ALT);
        JButton        btnSubmit    = createStyledButton("Create Account", COLOR_ACCENT_ALT);
        JButton        btnBack      = createLinkButton("← Back");

        btnSubmit.addActionListener(e -> {
            String name    = nameField.getText().trim();
            String user    = userField.getText().trim();
            String email   = emailField.getText().trim();
            String pass    = new String(passField.getPassword()).trim();
            String confirm = new String(confirmField.getPassword()).trim();

            if (name.isEmpty() || user.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                statusMsg.setText("All fields are required.");
            } else if (!email.contains("@") || !email.contains(".")) {
                statusMsg.setText("Please enter a valid email address.");
            } else if (pass.length() < 8) {
                statusMsg.setText("Password must be at least 8 characters.");
            } else if (!pass.equals(confirm)) {
                statusMsg.setText("Passwords don't match. Try again.");
            } else if (UserManager.userExists(user)) {
                statusMsg.setText("That username is already taken.");
            } else if (UserManager.emailExists(email)) {
                statusMsg.setText("That email is already registered.");
            } else if (UserManager.registerUser(name, user, email, pass)) {
                JOptionPane.showMessageDialog(frame,
                    "You're all set, " + name + "! Go ahead and log in.",
                    "Account Created",
                    JOptionPane.INFORMATION_MESSAGE
                );
                nameField.setText("");
                clearSignupFields(userField, emailField, passField, confirmField, statusMsg);
                cardLayout.show(rootPanel, "LOGIN");
            } else {
                statusMsg.setText("Registration failed. Please try again.");
            }
        });

        btnBack.addActionListener(e -> {
            nameField.setText("");
            clearSignupFields(userField, emailField, passField, confirmField, statusMsg);
            cardLayout.show(rootPanel, "WELCOME");
        });

        addToPanel(panel, createLabel("Create an account", 22, Font.BOLD, COLOR_TEXT), gbc, 0, 0, new Insets(0, 0, 20, 0));
        addToPanel(panel, makeLabeledField("Full Name",        nameField),    gbc, 0, 1, new Insets(0, 0, 10, 0));
        addToPanel(panel, makeLabeledField("Username",         userField),    gbc, 0, 2, new Insets(0, 0, 10, 0));
        addToPanel(panel, makeLabeledField("Email Address",    emailField),   gbc, 0, 3, new Insets(0, 0, 10, 0));
        addToPanel(panel, makeLabeledField("Password",         passField),    gbc, 0, 4, new Insets(0, 0, 10, 0));
        addToPanel(panel, makeLabeledField("Confirm Password", confirmField), gbc, 0, 5, new Insets(0, 0, 5,  0));
        addToPanel(panel, statusMsg,  gbc, 0, 6, new Insets(0, 0, 15, 0));
        addToPanel(panel, btnSubmit,  gbc, 0, 7, new Insets(0, 0, 10, 0));
        addToPanel(panel, btnBack,    gbc, 0, 8, new Insets(0, 0, 0,  0));

        return panel;
    }

    private JPanel createSearchScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BG);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_PANEL);
        header.setBorder(new EmptyBorder(15, 20, 15, 20));
        header.add(createLabel("🎌 Anime Discovery", 18, Font.BOLD, COLOR_TEXT), BorderLayout.WEST);

        JButton btnLogout = createLinkButton("Log out");
        btnLogout.addActionListener(e -> {
            cardLayout.show(rootPanel, "WELCOME");
        });
        header.add(btnLogout, BorderLayout.EAST);

        JPanel controls = new JPanel(new GridBagLayout());
        controls.setBackground(COLOR_BG);
        controls.setBorder(new EmptyBorder(18, 20, 8, 20));
        GridBagConstraints gbc = createGBC();
        gbc.weightx = 1.0;                    

        JLabel hint = createLabel("Type a genre or anime title below", 13, Font.PLAIN, COLOR_TEXT_DIM);

        JTextField searchField = createTextField("e.g. Action, Isekai, Naruto...");
        searchField.setPreferredSize(new Dimension(350, 45));

        JComboBox<String> yearBox   = createComboBox(createYearOptions());
        JComboBox<String> typeBox   = createComboBox(new String[] {
            "Any Type", "TV", "Movie", "OVA", "ONA", "Special", "Music"
        });
        JComboBox<String> originBox = createComboBox(new String[] {
            "Any Origin", "Japan", "China", "South Korea"
        });

        JButton btnFilter = createStyledButton("Filter", COLOR_ACCENT_ALT);
        btnFilter.setPreferredSize(new Dimension(95, 45));

        JPanel searchRow = new JPanel(new BorderLayout(8, 0));
        searchRow.setBackground(COLOR_BG);
        searchRow.add(searchField, BorderLayout.CENTER);
        searchRow.add(btnFilter, BorderLayout.EAST);

        JLabel selectedFilterText = createLabel("Filter: Any Year, Any Type, Any Origin", 11, Font.PLAIN, COLOR_TEXT_DIM);

        JDialog filterDialog = new JDialog(frame, "Filter Options", true);
        filterDialog.setSize(270, 315);
        filterDialog.setResizable(false);
        filterDialog.getContentPane().setBackground(COLOR_PANEL);

        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));
        filterPanel.setBackground(COLOR_PANEL);
        filterPanel.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel filterTitle = createLabel("Filter Options", 16, Font.BOLD, COLOR_TEXT);
        filterTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        yearBox.setMaximumSize(new Dimension(220, 36));
        typeBox.setMaximumSize(new Dimension(220, 36));
        originBox.setMaximumSize(new Dimension(220, 36));

        JButton btnApplyFilter = createStyledButton("Apply Filter", COLOR_ACCENT);
        btnApplyFilter.setPreferredSize(new Dimension(220, 40));
        btnApplyFilter.setMaximumSize(new Dimension(220, 40));

        filterPanel.add(filterTitle);
        filterPanel.add(Box.createVerticalStrut(15));
        filterPanel.add(makePopupFilterField("Year", yearBox));
        filterPanel.add(Box.createVerticalStrut(10));
        filterPanel.add(makePopupFilterField("Type", typeBox));
        filterPanel.add(Box.createVerticalStrut(10));
        filterPanel.add(makePopupFilterField("Origin", originBox));
        filterPanel.add(Box.createVerticalStrut(18));
        filterPanel.add(btnApplyFilter);

        filterDialog.add(filterPanel);

        btnFilter.addActionListener(e -> { filterDialog.setLocationRelativeTo(frame); filterDialog.setVisible(true);
        });

        btnApplyFilter.addActionListener(e -> {
            String year   = (String) yearBox.getSelectedItem();
            String type   = (String) typeBox.getSelectedItem();
            String origin = (String) originBox.getSelectedItem();

            selectedFilterText.setText("Filter: " + year + ", " + type + ", " + origin);
            filterDialog.setVisible(false);
        });

        JButton btnSearch = createStyledButton("Find Suggestions", COLOR_ACCENT);

        addToPanel(controls, hint,               gbc, 0, 0, new Insets(0, 0, 10, 0));
        addToPanel(controls, searchRow,          gbc, 0, 1, new Insets(0, 0, 7,  0));
        addToPanel(controls, selectedFilterText, gbc, 0, 2, new Insets(0, 0, 12, 0));
        addToPanel(controls, btnSearch,          gbc, 0, 3, new Insets(0, 0, 10, 0));

        cardContainer = new JPanel();
        cardContainer.setLayout(new BoxLayout(cardContainer, BoxLayout.Y_AXIS));
        cardContainer.setBackground(COLOR_BG);

        cardScroll = new JScrollPane(cardContainer);
        cardScroll.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_PANEL));
        cardScroll.getVerticalScrollBar().setUnitIncrement(16);
        cardScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        cardScroll.getViewport().setBackground(COLOR_BG);

        showPlaceholder("Your results will appear here.");

        btnSearch.addActionListener(e -> {
            String query = searchField.getText().trim();

            if (query.isEmpty() || query.equals("e.g. Action, Isekai, Naruto...")) {
                showPlaceholder("Please type an anime title or genre first.");
                return;
            }

            String year   = (String) yearBox.getSelectedItem();
            String type   = (String) typeBox.getSelectedItem();
            String origin = (String) originBox.getSelectedItem();

            showPlaceholder("Searching anime suggestions...");

            SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() {
                    AnimeEngine engine = new AnimeEngine();
                    StringWriter sw = new StringWriter();

                    PrintStream old = System.out;
                    PrintStream capture = new PrintStream(new OutputStream() {
                        public void write(int b) {
                            sw.write(b);
                        }
                    });

                    try {
                        System.setOut(capture);
                        engine.showSuggestions(query, year, type, origin);
                    } finally {
                        System.setOut(old);
                        capture.close();
                    }

                    return sw.toString();
                }

                @Override
                protected void done() {
                    try {
                        displayCards(get());
                    } catch (Exception ex) {
                        showPlaceholder("Something went wrong. Please try again.");
                    }
                }
            };

            worker.execute();
        });

        JPanel topHalf = new JPanel(new BorderLayout());
        topHalf.setBackground(COLOR_BG);
        topHalf.add(header,   BorderLayout.NORTH);
        topHalf.add(controls, BorderLayout.CENTER);

        panel.add(topHalf,    BorderLayout.NORTH);
        panel.add(cardScroll, BorderLayout.CENTER); 

        return panel;
    }

    private JPanel makePopupFilterField(String labelText, JComboBox<String> comboBox) {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBackground(COLOR_PANEL);
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setForeground(COLOR_TEXT_DIM);
        label.setFont(new Font("SansSerif", Font.PLAIN, 11));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(new EmptyBorder(0, 2, 3, 0));

        comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        wrapper.add(label);
        wrapper.add(comboBox);

        return wrapper;
    }

    private JComboBox<String> createComboBox(String[] values) {
        JComboBox<String> box = new JComboBox<>(values);
        box.setBackground(COLOR_FIELD);
        box.setForeground(COLOR_TEXT);
        box.setFont(new Font("SansSerif", Font.PLAIN, 11));
        box.setFocusable(false);
        box.setPreferredSize(new Dimension(110, 36));
        return box;
    }

    private String[] createYearOptions() {
        String[] years = new String[38];
        years[0] = "Any Year";
        int index = 1;
        for (int year = 2026; year >= 1990; year--) {
            years[index] = String.valueOf(year);
            index++;
        }
        return years;
    }

    private JPanel makeLabeledField(String labelText, JComponent field) {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBackground(COLOR_BG);
        wrapper.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setForeground(COLOR_TEXT_DIM);
        label.setFont(new Font("SansSerif", Font.PLAIN, 11));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(new EmptyBorder(0, 2, 3, 0));

        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(280, 40));
        field.setPreferredSize(new Dimension(280, 38));

        wrapper.add(label);
        wrapper.add(field);
        return wrapper;
    }

    private void displayCards(String raw) {
        cardContainer.removeAll();

        if (raw == null || raw.trim().isEmpty()) {
            showPlaceholder("Nothing came back. Try a different search.");
            return;
        }

        if (raw.contains("CONNECTION ERROR")) {
            showPlaceholder(raw.trim());
            return;
        }

        if (raw.contains("No results found.") || raw.contains("No results matched your selected filters.")) {
            showPlaceholder("No results matched your search or selected filters.");
            return;
        }

        String[] entries = raw.split("(?=\\d+\\. )");
        boolean anyCard = false;

        for (String entry : entries) {
            entry = entry.trim();
            if (entry.isEmpty()) continue;

            String[] lines = entry.split("\n");
            if (lines.length < 2) continue;

            String title      = lines[0].replaceAll("^\\d+\\.\\s*", "").trim();
            String released   = extractField(lines, "Released");
            String type       = extractField(lines, "Type");
            String episodes   = extractField(lines, "Episodes");
            String popularity = extractField(lines, "Popularity");
            String origin     = extractField(lines, "Origin");
            String imageUrl   = extractField(lines, "Image").replace("\\/", "/");

            if (title.isEmpty() || title.startsWith("Searching for:") || title.startsWith("---") || title.startsWith("No results")) {
                continue;
            }

            JPanel card = buildAnimeCard(title, released, type, episodes, popularity, origin, imageUrl);
            card.setAlignmentX(Component.LEFT_ALIGNMENT);
            cardContainer.add(card);
            cardContainer.add(Box.createVerticalStrut(8));
            anyCard = true;
        }

        if (!anyCard) {
            showPlaceholder("Nothing came back. Try a different search.");
        } else {
            cardContainer.revalidate();
            cardContainer.repaint();
            SwingUtilities.invokeLater(() -> cardScroll.getVerticalScrollBar().setValue(0)
            );
        }
    }

    private String extractField(String[] lines, String label) {
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.toLowerCase().startsWith(label.toLowerCase())) {
                int colon = trimmed.indexOf(':');
                if (colon >= 0) return trimmed.substring(colon + 1).trim();
            }
        }
        return "N/A";
    }

    private JPanel buildAnimeCard(String title, String released, String type, String episodes, String popularity, String origin, String imageUrl) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(new Color(40, 40, 62));
        card.setBorder(new CompoundBorder(
            new LineBorder(new Color(60, 60, 90), 1, true),
            new EmptyBorder(10, 10, 10, 10)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        JLabel imgLabel = new JLabel();
        imgLabel.setPreferredSize(new Dimension(65, 95));
        imgLabel.setOpaque(true);
        imgLabel.setBackground(new Color(30, 30, 50));
        imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imgLabel.setVerticalAlignment(SwingConstants.CENTER);
        imgLabel.setText("…");
        imgLabel.setForeground(COLOR_TEXT_DIM);
        imgLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));

        if (!imageUrl.equals("N/A") && !imageUrl.isEmpty()) {
            imgLabel.putClientProperty("expectedUrl", imageUrl);
            loadImageAsync(imageUrl, imgLabel);
        } else {
            imgLabel.setText("🎌");
        }

        JPanel info = new JPanel(new GridLayout(0, 1, 0, 3));
        info.setBackground(new Color(40, 40, 62));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(COLOR_TEXT);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 13));

        info.add(lblTitle);
        info.add(makeInfoRow("📅", "Released",  released));
        info.add(makeInfoRow("🎞", "Type",      type));
        info.add(makeInfoRow("🎬", "Episodes",  episodes));
        info.add(makeInfoRow("🌍", "Origin",    origin));
        info.add(makeInfoRow("🔥", "Buzz",      popularity));

        card.add(imgLabel, BorderLayout.WEST);
        card.add(info,     BorderLayout.CENTER);
        return card;
    }

    private JLabel makeInfoRow(String icon, String label, String value) {
        JLabel lbl = new JLabel(icon + "  " + label + ": " + value);
        lbl.setForeground(COLOR_TEXT_DIM);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        return lbl;
    }

    private void loadImageAsync(String urlStr, JLabel target) {
        new Thread(() -> {
            BufferedImage raw = fetchImage(urlStr, 3);
            if (raw != null) {
                BufferedImage scaled = new BufferedImage(65, 95, BufferedImage.TYPE_INT_ARGB);
                java.awt.Graphics2D g2 = scaled.createGraphics();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING, java.awt.RenderingHints.VALUE_RENDER_QUALITY);
                g2.drawImage(raw, 0, 0, 65, 95, null);
                g2.dispose();
                ImageIcon icon = new ImageIcon(scaled);
                SwingUtilities.invokeLater(() -> {
                    Object tag = target.getClientProperty("expectedUrl");
                    if (urlStr.equals(tag)) { target.setText(""); target.setIcon(icon);
                    }
                });
            } else {
                SwingUtilities.invokeLater(() -> {
                    Object tag = target.getClientProperty("expectedUrl");
                    if (urlStr.equals(tag)) target.setText("🎌");
                });
            }
        }).start();
    }

    private BufferedImage fetchImage(String urlStr, int redirectsLeft) {
        try {
            if (urlStr.startsWith("http://")) {
                urlStr = "https://" + urlStr.substring(7);
            }

            HttpURLConnection conn = (HttpURLConnection) URI.create(urlStr).toURL().openConnection();
            conn.setInstanceFollowRedirects(false);  
            conn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64)" + "AppleWebKit/537.36 (KHTML, like Gecko) " + "Chrome/124.0.0.0 Safari/537.36");
            conn.setRequestProperty("Accept", "image/avif,image/webp,image/apng,image/*,*/*;q=0.8");
            conn.setRequestProperty("Referer", "https://myanimelist.net/");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.connect();

            int status = conn.getResponseCode();
            if ((status == HttpURLConnection.HTTP_MOVED_TEMP|| status == HttpURLConnection.HTTP_MOVED_PERM|| status == 307 || status == 308) && redirectsLeft > 0) {
                String location = conn.getHeaderField("Location");
                conn.disconnect();
                if (location != null && !location.isEmpty()) {
                    return fetchImage(location, redirectsLeft - 1);
                }
                return null;
            }

            if (status == HttpURLConnection.HTTP_OK) {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                InputStream is = conn.getInputStream();
                byte[] chunk = new byte[4096];
                int n;
                while ((n = is.read(chunk)) != -1) buffer.write(chunk, 0, n);
                is.close();
                conn.disconnect();
                return ImageIO.read(new ByteArrayInputStream(buffer.toByteArray()));
            }

            conn.disconnect();
        } catch (Exception ignored) { }
        return null;
    }

    private void showPlaceholder(String message) {
        cardContainer.removeAll();
        JLabel lbl = new JLabel(message, SwingConstants.CENTER);
        lbl.setForeground(COLOR_TEXT_DIM);
        lbl.setFont(new Font("SansSerif", Font.ITALIC, 12));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardContainer.add(Box.createVerticalGlue());
        cardContainer.add(lbl);
        cardContainer.add(Box.createVerticalGlue());
        cardContainer.revalidate();
        cardContainer.repaint();
    }

    private JPanel createBasePanel(LayoutManager layout) {
        JPanel p = new JPanel(layout);
        p.setBackground(COLOR_BG);
        return p;
    }

    private JLabel createLabel(String text, int size, int style, Color color) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", style, size));
        lbl.setForeground(color);
        return lbl;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(280, 45));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(bg.brighter()); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(bg); }
        });
        return btn;
    }

    private JButton createLinkButton(String text) {
        JButton btn = new JButton(text);
        btn.setForeground(COLOR_TEXT_DIM);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JTextField createTextField(String hint) {
        JTextField tf = new JTextField(20);
        tf.setBackground(COLOR_FIELD);
        tf.setForeground(COLOR_TEXT);
        tf.setCaretColor(COLOR_ACCENT);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_PANEL),
            new EmptyBorder(5, 10, 5, 10)
        ));
        tf.setToolTipText(hint);
        addPlaceholder(tf, hint);
        return tf;
    }

    private JPasswordField createPasswordField(String hint) {
        JPasswordField pf = new JPasswordField(20);
        pf.setBackground(COLOR_FIELD);
        pf.setForeground(COLOR_TEXT);
        pf.setCaretColor(COLOR_ACCENT);
        pf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_PANEL),
            new EmptyBorder(5, 10, 5, 10)
        ));
        pf.setToolTipText(hint);
        return pf;
    }

    private void addPlaceholder(JTextField tf, String placeholder) {
        tf.setForeground(COLOR_TEXT_DIM);
        tf.setText(placeholder);

        tf.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (tf.getText().equals(placeholder)) {
                    tf.setText("");
                    tf.setForeground(COLOR_TEXT);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (tf.getText().isEmpty()) {
                    tf.setForeground(COLOR_TEXT_DIM);
                    tf.setText(placeholder);
                }
            }
        });
    }

    private GridBagConstraints createGBC() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    private void addToPanel(JPanel p, Component c, GridBagConstraints gbc, int x, int y, Insets insets) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.insets = insets;
        p.add(c, gbc);
    }

    private void clearLoginFields(JTextField u, JPasswordField p, JLabel s) {
        u.setText("");
        p.setText("");
        s.setText("");
    }

    private void clearSignupFields(JTextField user, JTextField email,   JPasswordField pass, JPasswordField confirm,  JLabel status) {
        user.setText("");
        email.setText("");
        pass.setText("");
        confirm.setText("");
        status.setText("");
    }
}