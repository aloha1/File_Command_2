package com.amaze.file_command.ui.drawer;

public class SectionItem implements Item{

	@Override
	public boolean isSection() {
		return true;
	}

	@Override
	public boolean isFragment() {
		return true;
	}
}
