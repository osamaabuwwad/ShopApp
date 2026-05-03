import javafx.application.Application;
import java.sql.PreparedStatement;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PCShopApp extends Application {

    private Scene loginScene, signUpScene;
    private double balance = 2500.00;
    private Label balanceLabel;
    private BorderPane mainLayout;
    private ScrollPane currentProductView;
    private Label pageTitleLabel;
    private VBox leftMenu; // Made this a class variable to access it in multiple methods
    
    // Cart related variables
    private List<CartItem> cartItems = new ArrayList<>();
    private VBox cartView;
    private Label cartTotalLabel;
    
    // Product data
    private final String[][] productNames = {
        {"Gaming PC Pro", "Workstation PC", "Budget PC"}, // PCs
        {"RGB Keyboard", "Wireless Mouse", "Gaming Headset"}, // Accessories
        {"Gaming Laptop", "Ultrabook", "Business Laptop"}  // Laptops
    };
    private final double[][] productPrices = {
        {999.99, 1299.99, 599.99}, // PCs
        {89.99, 49.99, 129.99},    // Accessories
        {1499.99, 999.99, 1199.99} // Laptops
    };
    private final String[][] imagePaths = {
        {
            "C:\\Users\\ACER\\Downloads\\p1 (1).png",
            "C:\\Users\\ACER\\Downloads\\p1 (2).png",
            "C:\\Users\\ACER\\Downloads\\p1 (3).png"
        },
        {
            "C:\\Users\\ACER\\Downloads\\mouse1.png",
            "C:\\Users\\ACER\\Downloads\\mouse2.png",
            "C:\\Users\\ACER\\Downloads\\mouse3.png"
        },
        {
            "C:\\Users\\ACER\\Downloads\\laptop1.png",
            "C:\\Users\\ACER\\Downloads\\laptop2.png",
            "C:\\Users\\ACER\\Downloads\\offer.png"
        }
    };
    
    // Cart item class
    private class CartItem {
        String name;
        double price;
        int quantity;
        String imagePath;
        int categoryIndex;
        int productIndex;

        public CartItem(String name, double price, String imagePath, int categoryIndex, int productIndex) {
            this.name = name;
            this.price = price;
            this.quantity = 1;
            this.imagePath = imagePath;
            this.categoryIndex = categoryIndex;
            this.productIndex = productIndex;
        }

        public double getTotal() {
            return price * quantity;
        }
    }

    public void start(Stage primaryStage) {
        // Initialize cart view
        initializeCartView();

        VBox loginRoot = new VBox(15);
        loginRoot.setPadding(new Insets(20));
        loginRoot.setStyle("-fx-background-color:#f0f0f0;");
        loginRoot.setAlignment(Pos.CENTER);
        loginRoot.setStyle("-fx-background-color: #2c3e50;");
        Label loginTitle = new Label("Login");
        loginTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        loginTitle.setStyle("-fx-text-fill: white;-fx-font-size: 24px; -fx-font-weight: bold;");
        TextField loginUser = new TextField();
        loginUser.setPromptText("Username");
        PasswordField loginPass = new PasswordField();
        loginPass.setPromptText("Password");
        Button loginBtn = new Button("Login");
        Button goToSignUp = new Button("Sign Up");
        Label loginMsg = new Label();

        HBox loginButtons = new HBox(10, loginBtn, goToSignUp);
        loginButtons.setAlignment(Pos.CENTER);

        loginRoot.getChildren().addAll(loginTitle, loginUser, loginPass, loginButtons, loginMsg);
        loginScene = new Scene(loginRoot, 400, 300);

        // === SIGN UP UI ===
        VBox signUpRoot = new VBox(15);
        signUpRoot.setPadding(new Insets(20));
        signUpRoot.setStyle("-fx-background-color:#2c3e50;");
        signUpRoot.setAlignment(Pos.CENTER);

        Label signUpTitle = new Label("Sign Up");
        signUpTitle.setStyle("-fx-text-fill: white;-fx-font-size: 24px; -fx-font-weight: bold;");
        TextField namefield= new TextField();
        namefield.setPromptText("Choose username");
        TextField emailfield = new TextField();
        emailfield.setPromptText("Enter your Email");
        PasswordField passfield = new PasswordField();
        passfield.setPromptText("Password");
        PasswordField passfield2 = new PasswordField();
        passfield2.setPromptText("Confirm password");
        Button registerBtn = new Button("Register");
        Button backToLogin = new Button("Back");
        Label signUpMsg = new Label();

        HBox signUpButtons = new HBox(10, registerBtn, backToLogin);
        signUpButtons.setAlignment(Pos.CENTER);

        signUpRoot.getChildren().addAll(signUpTitle, namefield,emailfield, passfield, passfield2, signUpButtons, signUpMsg);
        signUpScene = new Scene(signUpRoot, 400, 350);

        // --- TOP TITLE ---
        Text title = new Text("Gamers Paradise");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        title.setFill(Color.WHITE);
        
        // Page title label
        pageTitleLabel = new Label("Home");
        pageTitleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        pageTitleLabel.setTextFill(Color.WHITE);
        
        // Exit button (top right)
        Button exitBtn = new Button();
        exitBtn.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        try {
            Image exitIcon = new Image(new FileInputStream("C:\\Users\\ACER\\Downloads\\exit.png"));
            ImageView exitView = new ImageView(exitIcon);
            exitView.setFitHeight(30);
            exitView.setFitWidth(30);
            exitBtn.setGraphic(exitView);
        } catch (FileNotFoundException e) {
            exitBtn.setText("X");
            exitBtn.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        }
        exitBtn.setOnAction(e -> primaryStage.close());
        
        // Top bar with title and exit button
        HBox topBar = new HBox(title, exitBtn);
        topBar.setAlignment(Pos.CENTER);
        HBox.setHgrow(title, Priority.ALWAYS);
        topBar.setPadding(new Insets(20));
        topBar.setStyle("-fx-background-color: #2c3e50;");
 
        // --- LEFT MENU ---
        leftMenu = new VBox(15);
        leftMenu.setPadding(new Insets(20));
        leftMenu.setStyle("-fx-background-color: #34495e;");
        leftMenu.setPrefWidth(180);
        leftMenu.setVisible(false); // Initially hidden
 
        Button homeBtn = createMenuButton("Home Page");
        Button pcCasesBtn = createMenuButton("PC Cases");
        Button accessoriesBtn = createMenuButton("Accessories");
        Button laptopsBtn = createMenuButton("Laptops");
        Button cartBtn = createMenuButton("Cart");
 
        // Set actions for category buttons
        homeBtn.setOnAction(e -> {
            pageTitleLabel.setText("Home");
            leftMenu.setVisible(false); // Hide menu when going to home
            ScrollPane homePage = createHomePage();
            mainLayout.setCenter(homePage);
        });
        pcCasesBtn.setOnAction(e -> {
            pageTitleLabel.setText("PC Cases");
            leftMenu.setVisible(true); // Show menu for other pages
            showProducts(0);
        });
        accessoriesBtn.setOnAction(e -> {
            pageTitleLabel.setText("Accessories");
            leftMenu.setVisible(true);
            showProducts(1);
        });
        laptopsBtn.setOnAction(e -> {
            pageTitleLabel.setText("Laptops");
            leftMenu.setVisible(true);
            showProducts(2);
        });
        cartBtn.setOnAction(e -> {
            pageTitleLabel.setText("Cart");
            leftMenu.setVisible(true);
            showCart();
        });
 
        leftMenu.getChildren().addAll(homeBtn, pcCasesBtn, accessoriesBtn, laptopsBtn, cartBtn);
 
        // --- INITIAL HOME PAGE ---
        ScrollPane homePage = createHomePage();
 
        // --- BALANCE DISPLAY ---
        balanceLabel = new Label(String.format("Balance: $%.2f", balance));
        balanceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        balanceLabel.setTextFill(Color.WHITE);
        balanceLabel.setPadding(new Insets(10));
        balanceLabel.setStyle("-fx-background-color: #27ae60; -fx-background-radius: 5;");
 
        // --- MAIN LAYOUT ---
        mainLayout = new BorderPane();
        mainLayout.setTop(topBar);
        mainLayout.setLeft(leftMenu);
        mainLayout.setCenter(homePage);
        mainLayout.setBottom(balanceLabel);
        BorderPane.setAlignment(balanceLabel, Pos.CENTER_LEFT);
        BorderPane.setMargin(balanceLabel, new Insets(0, 0, 20, 20));
        mainLayout.setStyle("-fx-background-color: #bdc3c7;");
 
        // --- SCENE SETUP ---
        primaryStage.setTitle("Gamers Paradise - PC Shop");
        Scene mainScene = new Scene(mainLayout, 800, 600);
        
        //-----------------------
        loginBtn.setOnAction(e -> {
            try {
                //1- Connection
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/user2", "root", "");
                
                //2- Statement
                Statement st = con.createStatement();
                ResultSet result = st.executeQuery("SELECT * FROM usergister2");
                String name = null;
                Integer no = 0;
                boolean found = false;
                while (result.next()) {
                  while (result.next()) {
                    name=result.getString("name");
                    no=result.getInt("password");
                }
                    if(loginUser.getText().equalsIgnoreCase(name) && loginPass.getText().equals(no + "")){
                        primaryStage.setScene(mainScene);
                    }
                    else
                    {
                        Alert a=new Alert(Alert.AlertType.ERROR,"Invalid");
                        a.show();
                    }
                }
                if (found) {
                    primaryStage.setScene(mainScene);
                } else {
                    loginMsg.setText("Invalid credentials.");
                    loginMsg.setStyle("-fx-text-fill: white;");
                }
            } catch (Exception ex) {
                Alert a = new Alert(Alert.AlertType.ERROR, ex.getMessage());
                a.show();
            }
        });


        goToSignUp.setOnAction(e -> primaryStage.setScene(signUpScene));
        backToLogin.setOnAction(e -> primaryStage.setScene(loginScene));

        registerBtn.setOnAction(e -> {
            if (namefield.getText().isEmpty() || emailfield.getText().isEmpty() || passfield.getText().isEmpty() || !passfield.getText().equals(passfield2.getText())) {
                Alert s = new Alert(Alert.AlertType.ERROR, "Please fill all fields correctly");
                s.show();
            } else {
                try {
                    //1-connection url
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/user2", "root", "");
                    //2-prepared statment
                    PreparedStatement st = con.prepareStatement("INSERT INTO usergister2 (name ,email, password) VALUES(?,?,?)");
                    //3-parameters
                    st.setString(1, namefield.getText());
                    st.setString(2, emailfield.getText());
                    st.setString(3, passfield.getText());
                    //4=excute
                    st.execute();
                    Alert a = new Alert(Alert.AlertType.INFORMATION, "User registered successfully");
                    a.show();
                } catch (Exception ex) {
                    Alert b = new Alert(Alert.AlertType.INFORMATION, ex.getMessage());
                    b.show();
                }
            }
        });
        
          registerBtn.setOnAction(e -> {
            if (namefield.getText().isEmpty() || emailfield.getText().isEmpty() || passfield.getText().isEmpty() || !passfield.getText().equals(passfield2.getText())) {
                Alert s = new Alert(Alert.AlertType.ERROR, "Please fill all fields correctly");
                s.show();
            }
            else
            {
            try
            {
            Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/user2","root","");
            PreparedStatement st=con.prepareStatement("INSERT INTO usergister2 (name, email, password) VALUERS(?,?,?)");
            st.setString(1, namefield.getText());
            st.setString(2, emailfield.getText());
            st.setString(3, passfield.getText());
            st.execute();
            }
            catch(Exception ex)
            {
                Alert a=new Alert(Alert.AlertType.INFORMATION,ex.getMessage());
                a.show();
            }
            }
          });
        

        primaryStage.setScene(loginScene);
        primaryStage.setTitle("Login System");
        primaryStage.show();
    }

    private ScrollPane createHomePage() {
        FlowPane homeFlow = new FlowPane();
        homeFlow.setHgap(30);
        homeFlow.setVgap(30);
        homeFlow.setPadding(new Insets(30));
        homeFlow.setAlignment(Pos.CENTER);
        
        String[] categoryImages = {
            "C:\\Users\\ACER\\Downloads\\p1 (1).png", // PC image
            "C:\\Users\\ACER\\Downloads\\mouse1.png", // Accessories image
            "C:\\Users\\ACER\\Downloads\\laptop1.png" // Laptop image
        };
        
        String[] categoryNames = {"PC Cases", "Accessories", "Laptops"};
        
        for (int i = 0; i < 3; i++) {
            final int categoryIndex = i;
            try {
                StackPane categoryPane = new StackPane();
                categoryPane.setStyle("-fx-background-color: #ecf0f1; -fx-border-radius: 5; -fx-background-radius: 5;");
                
                FileInputStream input = new FileInputStream(categoryImages[i]);
                Image image = new Image(input);
                ImageView imageView = new ImageView(image);
                imageView.setFitHeight(200);
                imageView.setFitWidth(300);
                imageView.setPreserveRatio(true);
                
                // Create overlay label
                Label nameLabel = new Label(categoryNames[i]);
                nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
                nameLabel.setTextFill(Color.WHITE);
                nameLabel.setStyle("-fx-background-color: rgba(0,0,0,0.5); -fx-padding: 10px;");
                
                // Make image clickable
                imageView.setOnMouseClicked(e -> {
                    pageTitleLabel.setText(categoryNames[categoryIndex]);
                    leftMenu.setVisible(true); // Show menu when category is clicked
                    showProducts(categoryIndex);
                });
                
                StackPane.setAlignment(nameLabel, Pos.BOTTOM_CENTER);
                categoryPane.getChildren().addAll(imageView, nameLabel);
                homeFlow.getChildren().add(categoryPane);
                
            } catch (FileNotFoundException e) {
                System.out.println("Could not load image: " + categoryImages[i]);
                VBox placeholderCard = createPlaceholderCard();
                homeFlow.getChildren().add(placeholderCard);
            }
        }
        
        ScrollPane scrollPane = new ScrollPane(homeFlow);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: #bdc3c7; -fx-border-color: #bdc3c7;");
        return scrollPane;
    }

    private void initializeCartView() {
        cartView = new VBox(10);
        cartView.setPadding(new Insets(20));
        cartView.setStyle("-fx-background-color: #ecf0f1;");
        
        Label cartTitle = new Label("Your Shopping Cart");
        cartTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        cartTotalLabel = new Label("Total: $0.00");
        cartTotalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        Button checkoutBtn = new Button("Checkout");
        checkoutBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        checkoutBtn.setOnAction(e -> checkout());
        
        Button clearCartBtn = new Button("Clear Cart");
        clearCartBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        clearCartBtn.setOnAction(e -> clearCart());
        
        VBox cartControls = new VBox(10, cartTotalLabel, checkoutBtn, clearCartBtn);
        cartControls.setAlignment(Pos.CENTER);
        
        cartView.getChildren().addAll(cartTitle, cartControls);
    }
    
    private void showCart() {
        cartView.getChildren().remove(1, cartView.getChildren().size()); // Clear existing items
        
        if (cartItems.isEmpty()) {
            Label emptyLabel = new Label("Your cart is empty");
            emptyLabel.setFont(Font.font("Arial", 16));
            cartView.getChildren().add(1, emptyLabel);
        } else {
            ScrollPane scrollPane = new ScrollPane();
            VBox itemsContainer = new VBox(10);
            
            for (CartItem item : cartItems) {
                HBox itemBox = new HBox(15);
                itemBox.setPadding(new Insets(10));
                itemBox.setStyle("-fx-background-color: white; -fx-background-radius: 5;");
                itemBox.setAlignment(Pos.CENTER_LEFT);
                
                try {
                    ImageView imageView = new ImageView(new Image(new FileInputStream(item.imagePath)));
                    imageView.setFitHeight(60);
                    imageView.setFitWidth(80);
                    itemBox.getChildren().add(imageView);
                } catch (FileNotFoundException e) {
                    // Use placeholder if image not found
                    ImageView placeholder = new ImageView();
                    placeholder.setFitHeight(60);
                    placeholder.setFitWidth(80);
                    placeholder.setStyle("-fx-background-color: #b2bec3;");
                    itemBox.getChildren().add(placeholder);
                }
                
                VBox itemDetails = new VBox(5);
                Label nameLabel = new Label(item.name);
                nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                Label priceLabel = new Label(String.format("$%.2f x %d", item.price, item.quantity));
                priceLabel.setFont(Font.font("Arial", 12));
                itemDetails.getChildren().addAll(nameLabel, priceLabel);
                
                HBox quantityControls = new HBox(5);
                Button decreaseBtn = new Button("-");
                decreaseBtn.setOnAction(e -> {
                    if (item.quantity > 1) {
                        item.quantity--;
                        showCart();
                    } else {
                        cartItems.remove(item);
                        showCart();
                    }
                });
                
                Button increaseBtn = new Button("+");
                increaseBtn.setOnAction(e -> {
                    item.quantity++;
                    showCart();
                });
                
                Button buyItemBtn = new Button("Buy Now");
                buyItemBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                buyItemBtn.setOnAction(e -> {
                    buyNow(item.categoryIndex, item.productIndex);
                    cartItems.remove(item);
                    showCart();
                });
                
                quantityControls.getChildren().addAll(decreaseBtn, increaseBtn);
                quantityControls.setAlignment(Pos.CENTER);
                
                Label totalLabel = new Label(String.format("$%.2f", item.getTotal()));
                totalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                
                itemBox.getChildren().addAll(itemDetails, quantityControls, totalLabel, buyItemBtn);
                HBox.setHgrow(itemDetails, Priority.ALWAYS);
                
                itemsContainer.getChildren().add(itemBox);
            }
            
            scrollPane.setContent(itemsContainer);
            scrollPane.setFitToWidth(true);
            
            Label cartTitle = new Label("Your Cart");
            cartTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
            cartTitle.setPadding(new Insets(0, 0, 10, 0));
            
            VBox container = new VBox(10, cartTitle, scrollPane);
            cartView.getChildren().add(1, container);
        }
        
        updateCartTotal();
        
        // Create a container with the page title and cart content
        VBox container = new VBox(10, pageTitleLabel, cartView);
        container.setPadding(new Insets(20));
        mainLayout.setCenter(container);
    }
    
    private void updateCartTotal() {
        double total = cartItems.stream().mapToDouble(CartItem::getTotal).sum();
        cartTotalLabel.setText(String.format("Total: $%.2f", total));
    }
    
    private void checkout() {
        double total = cartItems.stream().mapToDouble(CartItem::getTotal).sum();
        
        if (total > balance) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Insufficient Funds");
            alert.setHeaderText(null);
            alert.setContentText("You don't have enough balance to complete this purchase.");
            alert.showAndWait();
            return;
        }
        
        balance -= total;
        updateBalance();
        cartItems.clear();
        showCart();
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Purchase Complete");
        alert.setHeaderText(null);
        alert.setContentText(String.format("Thank you for your purchase! $%.2f has been deducted from your balance. Purchased %d items.", 
            total, cartItems.stream().mapToInt(item -> item.quantity).sum()));
        alert.showAndWait();
    }
    
    private void clearCart() {
        cartItems.clear();
        showCart();
    }
    
    private void buyNow(int categoryIndex, int productIndex) {
        double price = productPrices[categoryIndex][productIndex];
        
        if (price > 2500) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Purchase Error");
            alert.setHeaderText(null);
            alert.setContentText("This product exceeds the maximum allowed price of $2500.");
            alert.showAndWait();
            return;
        }
        
        if (price > balance) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Insufficient Funds");
            alert.setHeaderText(null);
            alert.setContentText("You don't have enough balance to purchase this item.");
            alert.showAndWait();
            return;
        }
        
        balance -= price;
        updateBalance();
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Purchase Complete");
        alert.setHeaderText(null);
        alert.setContentText(String.format("Thank you for your purchase! $%.2f has been deducted from your balance.", price));
        alert.showAndWait();
    }
    
    private void addToCart(int categoryIndex, int productIndex) {
        String name = productNames[categoryIndex][productIndex];
        double price = productPrices[categoryIndex][productIndex];
        String imagePath = imagePaths[categoryIndex][productIndex];
        
        // Check if item already in cart
        for (CartItem item : cartItems) {
            if (item.name.equals(name) && item.price == price) {
                item.quantity++;
                showCart();
                return;
            }
        }
        
        // Add new item to cart
        CartItem newItem = new CartItem(name, price, imagePath, categoryIndex, productIndex);
        cartItems.add(newItem);
        showCart();
    }

    private void showProducts(int categoryIndex) {
        String[] categoryTitles = {"PC Cases", "Accessories", "Laptops"};
        
        currentProductView = createProductGrid(categoryIndex);
        
        // Create a container with the page title and product grid
        VBox container = new VBox(10, pageTitleLabel, currentProductView);
        container.setPadding(new Insets(20));
        mainLayout.setCenter(container);
    }

    private ScrollPane createProductGrid(int categoryIndex) {
        GridPane productGrid = new GridPane();
        productGrid.setHgap(30);
        productGrid.setVgap(30);
        productGrid.setPadding(new Insets(30));
        productGrid.setAlignment(Pos.CENTER);
 
        for (int i = 0; i < productNames[categoryIndex].length; i++) {
            try {
                final int catIndex = categoryIndex;
                final int prodIndex = i;
                
                // Create product card
                VBox productCard = new VBox(10);
                productCard.setAlignment(Pos.CENTER);
                productCard.setPadding(new Insets(15));
                productCard.setStyle("-fx-background-color: #ecf0f1; -fx-border-radius: 5; -fx-background-radius: 5;");
 
                // Product image
                FileInputStream input = new FileInputStream(imagePaths[categoryIndex][i]);
                Image image = new Image(input);
                ImageView imageView = new ImageView(image);
                imageView.setFitHeight(120);
                imageView.setFitWidth(180);
                imageView.setPreserveRatio(true);
 
                // Product info
                Label nameLabel = new Label(productNames[categoryIndex][i]);
                nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
                Label priceLabel = new Label(String.format("$%.2f", productPrices[categoryIndex][i]));
                priceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                priceLabel.setTextFill(Color.web("#e74c3c"));
 
                // Add to cart button
                Button addToCartBtn = new Button("Add to Cart");
                addToCartBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
                addToCartBtn.setOnAction(e -> {
                    addToCart(catIndex, prodIndex);
                });
                
                // Buy Now button
                Button buyNowBtn = new Button("Buy Now");
                buyNowBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                buyNowBtn.setOnAction(e -> {
                    buyNow(catIndex, prodIndex);
                });
                
                // Button container
                HBox buttonBox = new HBox(10, addToCartBtn, buyNowBtn);
                buttonBox.setAlignment(Pos.CENTER);
 
                productCard.getChildren().addAll(imageView, nameLabel, priceLabel, buttonBox);
                productGrid.add(productCard, i % 3, i / 3);
 
            } catch (FileNotFoundException e) {
                System.out.println("Could not load image: " + imagePaths[categoryIndex][i]);
                VBox placeholderCard = createPlaceholderCard();
                productGrid.add(placeholderCard, i % 3, i / 3);
            }
        }
 
        ScrollPane scrollPane = new ScrollPane(productGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #bdc3c7; -fx-border-color: #bdc3c7;");
        return scrollPane;
    }
    
    private void updateBalance() {
        balanceLabel.setText(String.format("Balance: $%.2f", balance));
    }
 
    private Button createMenuButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(40);
        btn.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-font-size: 14;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #1abc9c; -fx-text-fill: white;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white;"));
        return btn;
    }
 
    private VBox createPlaceholderCard() {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #dfe6e9; -fx-border-radius: 5; -fx-background-radius: 5;");
        ImageView placeholder = new ImageView();
        placeholder.setFitHeight(120);
        placeholder.setFitWidth(180);
        placeholder.setStyle("-fx-background-color: #b2bec3;");
        Label nameLabel = new Label("Product Unavailable");
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        card.getChildren().addAll(placeholder, nameLabel);
        return card;
    }

    public static void main(String[] args) {
        launch(args);
    }
}