package com.dna.radius.infrastructure;


import com.dna.radius.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * this is a waiting fragment - does nothing except for showing a progress bar on the screen.
 * @author dror
 *
 */
public class WaitingFragment extends Fragment{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.waiting_fragment,container, false);
	}
}
