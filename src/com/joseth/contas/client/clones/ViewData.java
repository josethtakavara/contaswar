package com.joseth.contas.client.clones;

class ViewData {

    private boolean isEditing;

    /**
     * If true, this is not the first edit.
     */
    private boolean isEditingAgain;

    /**
     * Keep track of the original value at the start of the edit, which might be
     * the edited value from the previous edit and NOT the actual value.
     */
    private String original;

    private String text;

    /**
     * Construct a new ViewData in editing mode.
     *
     * @param text the text to edit
     */
    public ViewData(String text) {
      this.original = text;
      this.text = text;
      this.isEditing = true;
      this.isEditingAgain = false;
    }

    @Override
    public boolean equals(Object o) {
      if (o == null) {
        return false;
      }
      ViewData vd = (ViewData) o;
      return equalsOrBothNull(original, vd.original)
          && equalsOrBothNull(text, vd.text) && isEditing == vd.isEditing
          && isEditingAgain == vd.isEditingAgain;
    }

    public String getOriginal() {
      return original;
    }

    public String getText() {
      return text;
    }

    @Override
    public int hashCode() {
      return original.hashCode() + text.hashCode()
          + Boolean.valueOf(isEditing).hashCode() * 29
          + Boolean.valueOf(isEditingAgain).hashCode();
    }

    public boolean isEditing() {
      return isEditing;
    }

    public boolean isEditingAgain() {
      return isEditingAgain;
    }

    public void setEditing(boolean isEditing) {
      boolean wasEditing = this.isEditing;
      this.isEditing = isEditing;

      // This is a subsequent edit, so start from where we left off.
      if (!wasEditing && isEditing) {
        isEditingAgain = true;
        original = text;
      }
    }

    public void setText(String text) {
      this.text = text;
    }

    private boolean equalsOrBothNull(Object o1, Object o2) {
      return (o1 == null) ? o2 == null : o1.equals(o2);
    }
  }