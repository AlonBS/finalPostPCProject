package testing_stuff;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ListView;

public class ListViewWithMaxDimensions extends ListView{

	private int maxHeight, maxWidth;
	private boolean maxHeightDefined, maxWidthDefined;
	
	public ListViewWithMaxDimensions(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		maxHeightDefined = false;
		maxWidthDefined = false;
	}
	
	
	public void setMaxHeight(int maxHeight){
		maxHeightDefined = true;
		this.maxHeight = maxHeight;
	}
	
	public void setMaxWidth(int maxWidth){
		maxWidthDefined = true;
		this.maxWidth = maxWidth;
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
//		float convertedW = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) w, getResources().getDisplayMetrics());
//		float convertedH = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) h, getResources().getDisplayMetrics());
//		
		if(h > maxHeight){
			getLayoutParams().height = maxHeight;
		}
		if(w > maxWidth){
			getLayoutParams().width = maxWidth;
		}
		
	}

}
