package com.joseth.contas.client.clones;
import static com.google.gwt.dom.client.BrowserEvents.BLUR;
import static com.google.gwt.dom.client.BrowserEvents.CLICK;
import static com.google.gwt.dom.client.BrowserEvents.KEYDOWN;
import static com.google.gwt.dom.client.BrowserEvents.KEYUP;

import com.google.gwt.cell.client.AbstractEditableCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;
import com.google.gwt.user.client.Window;

/**
 * An editable text cell. Click to edit, escape to cancel, return to commit.
 */
public class ContasEditTextCell extends
    AbstractEditableCell<String, ViewData> {

  interface Template extends SafeHtmlTemplates {
    @Template("<input type=\"text\" value=\"{0}\" tabindex=\"-1\"></input>")
    SafeHtml input(String value);
  }

  private static Template template;

  private final SafeHtmlRenderer<String> renderer;

  /**
   * Construct a new EditTextCell that will use a
   * {@link SimpleSafeHtmlRenderer}.
   */
  public ContasEditTextCell() {
    this(SimpleSafeHtmlRenderer.getInstance());
  }

  /**
   * Construct a new EditTextCell that will use a given {@link SafeHtmlRenderer}
   * to render the value when not in edit mode.
   * 
   * @param renderer a {@link SafeHtmlRenderer SafeHtmlRenderer<String>}
   *          instance
   */
  public ContasEditTextCell(SafeHtmlRenderer<String> renderer) {
    super(CLICK, KEYUP, KEYDOWN, BLUR);
    if (template == null) {
      template = GWT.create(Template.class);
    }
    if (renderer == null) {
      throw new IllegalArgumentException("renderer == null");
    }
    this.renderer = renderer;
  }

  @Override
  public boolean isEditing(Context context, Element parent, String value) {
    ViewData viewData = getViewData(context.getKey());
    return viewData == null ? false : viewData.isEditing();
  }


  
  @Override
  public void onBrowserEvent(Context context, Element parent, String value,
      NativeEvent event, ValueUpdater<String> valueUpdater) {
	

	  Object key = context.getKey();
    ViewData viewData = getViewData(key);
    if (viewData != null && viewData.isEditing()) {
      // Handle the edit event.
      editEvent(context, parent, value, viewData, event, valueUpdater);
    } else {
      String type = event.getType();
      int keyCode = event.getKeyCode();
      boolean enterPressed = KEYUP.equals(type)
          && keyCode == KeyCodes.KEY_ENTER;
      if (CLICK.equals(type) || enterPressed) {
        // Go into edit mode.
        if (viewData == null) {
          viewData = new ViewData(value);
          setViewData(key, viewData);
        } else {
          viewData.setEditing(true);
        }
        edit(context, parent, value);
      }
    }
  }

  @Override
  public void render(Context context, String value, SafeHtmlBuilder sb) {
    // Get the view data.
    Object key = context.getKey();
    ViewData viewData = getViewData(key);
    if (viewData != null && !viewData.isEditing() && value != null
        && value.equals(viewData.getText())) {
      clearViewData(key);
      viewData = null;
    }

    String toRender = value;
    if (viewData != null) {
//      String text = viewData.getText();
      if (viewData.isEditing()) {
        /*
         * Do not use the renderer in edit mode because the value of a text
         * input element is always treated as text. SafeHtml isn't valid in the
         * context of the value attribute.
         */
//        sb.append(template.input(text));
        sb.append(template.input(toRender));
        return;
      }
//      else {
//        // The user pressed enter, but view data still exists.
//        toRender = text;
//      }
    }

    if (toRender != null && toRender.trim().length() > 0) {
      sb.append(renderer.render(toRender));
    } else {
      /*
       * Render a blank space to force the rendered element to have a height.
       * Otherwise it is not clickable.
       */
      sb.appendHtmlConstant("\u00A0");
    }
  }

  @Override
  public boolean resetFocus(Context context, Element parent, String value) {
    if (isEditing(context, parent, value)) {
      getInputElement(parent).focus();
      return true;
    }
    return false;
  }

  /**
   * Convert the cell to edit mode.
   *
   * @param context the {@link Context} of the cell
   * @param parent the parent element
   * @param value the current value
   */
  protected void edit(Context context, Element parent, String value) {
    setValue(context, parent, value);
    InputElement input = getInputElement(parent);
    input.focus();
    input.select();
  }

  /**
   * Convert the cell to non-edit mode.
   * 
   * @param context the context of the cell
   * @param parent the parent Element
   * @param value the value associated with the cell
   */
  private void cancel(Context context, Element parent, String value) {
    clearInput(getInputElement(parent));
    setValue(context, parent, value);
  }

  /**
   * Clear selected from the input element. Both Firefox and IE fire spurious
   * onblur events after the input is removed from the DOM if selection is not
   * cleared.
   *
   * @param input the input element
   */
  private native void clearInput(Element input) /*-{
    if (input.selectionEnd)
      input.selectionEnd = input.selectionStart;
    else if ($doc.selection)
      $doc.selection.clear();
  }-*/;

  /**
   * Commit the current value.
   * 
   * @param context the context of the cell
   * @param parent the parent Element
   * @param viewData the {@link ViewData} object
   * @param valueUpdater the {@link ValueUpdater}
   */
  private void commit(Context context, Element parent, ViewData viewData,
      ValueUpdater<String> valueUpdater) {
    String value = updateViewData(parent, viewData, false);
    clearInput(getInputElement(parent));
    setValue(context, parent, viewData.getOriginal());
    if (valueUpdater != null) {
      valueUpdater.update(value);
    }
  }

  private void editEvent(Context context, Element parent, String value,
      ViewData viewData, NativeEvent event, ValueUpdater<String> valueUpdater) {
    String type = event.getType();
    boolean keyUp = KEYUP.equals(type);
    boolean keyDown = KEYDOWN.equals(type);
    if (keyUp || keyDown) {
      int keyCode = event.getKeyCode();
      if (keyUp && keyCode == KeyCodes.KEY_ENTER) {
        // Commit the change.
        commit(context, parent, viewData, valueUpdater);
      } else if (keyUp && keyCode == KeyCodes.KEY_ESCAPE) {
        // Cancel edit mode.
        String originalText = viewData.getOriginal();
        if (viewData.isEditingAgain()) {
          viewData.setText(originalText);
          viewData.setEditing(false);
        } else {
          setViewData(context.getKey(), null);
        }
        cancel(context, parent, value);
      } else {
        // Update the text in the view data on each key.
        updateViewData(parent, viewData, true);
      }
    } else if (BLUR.equals(type)) {
      // Commit the change. Ensure that we are blurring the input element and
      // not the parent element itself.
      EventTarget eventTarget = event.getEventTarget();
      if (Element.is(eventTarget)) {
        Element target = Element.as(eventTarget);
        if ("input".equals(target.getTagName().toLowerCase())) {
          commit(context, parent, viewData, valueUpdater);
        }
      }
    }
  }

  /**
   * Get the input element in edit mode.
   */
  private InputElement getInputElement(Element parent) {
    return parent.getFirstChild().<InputElement> cast();
  }

  /**
   * Update the view data based on the current value.
   *
   * @param parent the parent element
   * @param viewData the {@link ViewData} object to update
   * @param isEditing true if in edit mode
   * @return the new value
   */
  private String updateViewData(Element parent, ViewData viewData,
      boolean isEditing) {
    InputElement input = (InputElement) parent.getFirstChild();
    String value = input.getValue();
    viewData.setText(value);
    viewData.setEditing(isEditing);
    return value;
  }
}