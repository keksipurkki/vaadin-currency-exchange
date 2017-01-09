package com.keksipurkki;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Theme;
import com.vaadin.spring.annotation.SpringUI;

import com.vaadin.ui.*;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.Page;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.ObjectProperty;

import java.math.BigDecimal;
import java.util.Locale;

/**
 *
 * The Vaadin-powered user interface for the app
 *
 */
@SpringUI
@Theme("valo")
public class CurrencyExchangeUI extends UI {

  CssLayout root;

  final BeanItemContainer currencyRates;

  private final String DIALOG_WIDTH = "80%"; // of the viewport
  private final String TITLE = "Valuuttamuunnin";

  private final BigDecimal DEFAULT_AMOUNT = BigDecimal.ONE;
  private final Currency DEFAULT_CURRENCY = CurrencyRepository.BASE_CURRENCY;

  private ObjectProperty<BigDecimal> amount = new ObjectProperty(DEFAULT_AMOUNT);
  private ObjectProperty<String> conversionEquation = new ObjectProperty("");
  private ComboBox[] currencies = new ComboBox[2];

  private VerticalLayout pageLayout;

  @Autowired
  public CurrencyExchangeUI(CurrencyRepository repo) {

    root = new CssLayout();

    /* Load the models into the view */
    currencyRates = new BeanItemContainer(Currency.class, repo.findAll());

  }

  @Override
  protected void init(VaadinRequest request) {

    UI.getCurrent().setLocale(new Locale("fi"));

    /* Configure the UI window */
    editPage(Page.getCurrent());

    setContent(root);
    root.addStyleName("root");
    root.setSizeFull();

    Label bg = new Label();
    bg.setSizeUndefined();
    bg.addStyleName("page-bg");
    root.addComponent(bg);

    /* The main content: dialog */
    pageLayout = new VerticalLayout();
    pageLayout.setSizeFull();
    pageLayout.setMargin(true);
    root.addComponent(pageLayout);

    Panel dialog = currencyExchangeDialog(TITLE);
    pageLayout.addComponent(dialog);
    pageLayout.setComponentAlignment(dialog, Alignment.MIDDLE_CENTER);

    Label copyright = new Label("© Elias Toivanen");
    copyright.setSizeUndefined();
    copyright.addStyleName("h5");

    pageLayout.addComponent(copyright);
    pageLayout.setComponentAlignment(copyright, Alignment.BOTTOM_RIGHT);

    doConversion(null);

  }

  /**
   *
   * Updates the `conversionEquation` based on current selection
   *
   */
  private void doConversion(Object e) {

    Currency from = (Currency)currencies[0].getValue();
    Currency to = (Currency)currencies[1].getValue();
    BigDecimal multiplier = amount.getValue();

    if (multiplier == null) return;

    if (multiplier.compareTo(BigDecimal.ZERO) >= 0) {
    
      BigDecimal conversion = Currency.convert(amount.getValue(), from, to);
      conversionEquation.setValue(
          String.format("%f %s = %f %s", amount.getValue(), from.getSymbol(),
            conversion,  to.getSymbol())
      );

    } else {

      Notification.show("Määrän on oltava positiivinen luku");
    
    }
  
  }

  /**
   * 
   * Swaps the currencies
   *
   */
  private void doSwapCurrencies(Object e) {
    ComboBox tmp = currencies[1];
    currencies[1] = currencies[0];
    currencies[0] = tmp;
    doConversion(e);
  }

  /**
   *
   * Restores the initial view
   * 
   */
  private void doResetView(Object e) {
    amount.setValue(DEFAULT_AMOUNT);
    resetComboBox(currencies[0]);
    resetComboBox(currencies[1]);
  }

  /* @todo: how to use the DEFAULT_CURRENCY as a reset value? */
  private void resetComboBox(ComboBox box) {
    box.setValue(currencyRates.getIdByIndex(0));
  }

  /**
   * 
   * Assembles the dialog for currency exchange calculations
   *
   */
  private Panel currencyExchangeDialog(String title) {

    /* dropdown like comboboxes for the currency selection */
    currencies[0] = currencyComboBox();
    currencies[1] = currencyComboBox();

    final Panel dialog = new Panel(title);
    dialog.addStyleName("dialog");
    dialog.addStyleName("dialog_currency-converter");
    dialog.setWidth(DIALOG_WIDTH);

    final VerticalLayout dialogContent = new VerticalLayout();

    /* Input fields + buttons */
    HorizontalLayout fields = new HorizontalLayout();
    fields.setSpacing(true);
    fields.setMargin(true);
    fields.addStyleName("fields");

    final TextField amountField = new TextField("Määrä", amount);
    amountField.setNullRepresentation("");
    amountField.addValueChangeListener(this::doConversion);
    amountField.focus();
    fields.addComponent(amountField);

    fields.addComponent(currencies[0]);

    final Button swap = new Button("", FontAwesome.EXCHANGE);
    fields.addComponent(swap);
    fields.setComponentAlignment(swap, Alignment.BOTTOM_LEFT);
    swap.addClickListener(this::doSwapCurrencies);

    fields.addComponent(currencies[1]);

    final Button reset = new Button("Tyhjennä");
    fields.addComponent(reset);
    fields.setComponentAlignment(reset, Alignment.BOTTOM_LEFT);
    reset.addClickListener(this::doResetView);

    dialogContent.addComponent(fields);
    dialogContent.setComponentAlignment(fields, Alignment.TOP_CENTER);

    /* The currency conversion equation */
    HorizontalLayout equation = conversionEquationView();
    equation.setWidth("80%");

    /* Mount dialog to pageLayout and wrap up */
    dialogContent.addComponent(equation);
    dialogContent.setComponentAlignment(equation, Alignment.BOTTOM_CENTER);

    dialog.setContent(dialogContent);

    return dialog;

  }

  /**
   *
   * A combo box for selecting a currency
   *
   */
  private ComboBox currencyComboBox() {

    ComboBox out = new ComboBox("Valuutta");
    out.setNullSelectionAllowed(false);
    out.setNewItemsAllowed(false);
    out.setTextInputAllowed(false);
    out.setItemCaptionPropertyId("symbol");

    out.setContainerDataSource(currencyRates);
    resetComboBox(out);

    out.addValueChangeListener(this::doConversion);

    return out;
  
  }

  /**
   *
   * An equation showing the result of the currency exchange
   *
   */
  private HorizontalLayout conversionEquationView() {

    Label text = new Label(conversionEquation);
    text.setSizeUndefined();
    text.addStyleName("h2");

    HorizontalLayout equation = new HorizontalLayout(
        text
    );

    equation.setComponentAlignment(text, Alignment.MIDDLE_CENTER);
    equation.setSpacing(true);
    equation.setMargin(true);

    return equation;
  
  }

  private void editPage(Page page) {
    page.setTitle(TITLE);
  }

}
