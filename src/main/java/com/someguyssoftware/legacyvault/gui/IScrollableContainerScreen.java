/**
 * 
 */
package com.someguyssoftware.legacyvault.gui;

/**
 * @author Mark Gottschling on May 18, 2021
 *
 */
public interface IScrollableContainerScreen {

	int getDisplayRowCount();

	int getDisplayColumnCount();

	int getScrollbarTopOffset();

	int getScrollbarHeight();

	int getSliderHeight();

	int getSliderWidth();

	int getScrollbarLeftOffset();

}
