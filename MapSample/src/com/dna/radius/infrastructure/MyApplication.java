package com.dna.radius.infrastructure;
import org.acra.*;
import org.acra.annotation.*;

import com.dna.radius.R;

import android.app.Application;

@ReportsCrashes(
		formKey = "", // This is required for backward compatibility but not used
		mailTo = "reports@yourdomain.com",
        mode = ReportingInteractionMode.SILENT
		)
public class MyApplication extends Application{

	@Override
	public void onCreate() {
		super.onCreate();

		// The following line triggers the initialization of ACRA
		ACRA.init(this);
	}
}
