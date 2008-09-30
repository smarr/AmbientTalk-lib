/**
 * AmbientTalk/2 Project
 * Profile.java created on 5 sep 2008 at 16:41:29
 * (c) Programming Technology Lab, 2006 - 2007
 * Authors: Tom Van Cutsem & Stijn Mostinckx
 * 
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package at.urbiflock.ui;

import java.util.Calendar;
import java.util.HashMap;

import edu.vub.at.objects.natives.NATBoolean;
import edu.vub.at.objects.natives.NATText;
import edu.vub.at.objects.natives.grammar.AGSymbol;

public interface Profile {
	
  public HashMap propertyHashMap();
  public boolean isMandatoryField(AGSymbol symbol);
  public void addField(AGSymbol fieldName, Object value, Object fieldType);
  public void removeField(AGSymbol fieldName);
  public void setField(AGSymbol fieldName, Object value);
  public String username();
  public AbstractFieldType[] possibleTypes();
  public AbstractFieldType getFieldType(AGSymbol fieldName);
  public AbstractFieldType makeStringFieldTypeObject();
  public AbstractFieldType makeEnumerationFieldTypeObject(Object[] possibleValues);
  public AbstractFieldType makeIntegerTypeFieldObject(int low, int high);
  public AbstractFieldType makeDateTypeFieldObject(Object low, Object high);
  public void setFieldShouldMatch(AGSymbol fieldName);
  public void setFieldShouldNotMatch(AGSymbol fieldName);
  public boolean fieldShouldMatch(AGSymbol fieldName);
}