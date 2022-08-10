package com.prpinfo.bancodesolucoes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public abstract class LookupActivityListAdapter extends ArrayAdapter<Solution> {
  private final Context context;
  private final ArrayList<Solution> values;

  private class ClickHandler implements View.OnClickListener {
    private final Solution solution;

    ClickHandler(Solution inputSolution) {
      solution = inputSolution;
    }

    @Override
    public void onClick(View view) {
      callback(solution);
    }
  }

  LookupActivityListAdapter(Context inputContext, ArrayList<Solution> inputValues) {
    super(inputContext, R.layout.activity_solution_lookup_list_adapter, inputValues);
    context = inputContext;
    values = inputValues;

    SqliteCore sqliteCore = new SqliteCore(context);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View rowView = convertView;

    if (convertView == null) {
      rowView = inflater.inflate(R.layout.activity_solution_lookup_list_adapter, parent, false);
    }
    LinearLayout rowContainer = rowView.findViewById(R.id.rowContainer);
    TextView
            category = rowView.findViewById(R.id.category),
            problem = rowView.findViewById(R.id.problem),
            solution = rowView.findViewById(R.id.solution);
    ImageView approved = rowView.findViewById(R.id.approved);
    try {
      Solution currentSolution = values.get(position);
      category.setText(currentSolution.category_description);
      problem.setText(currentSolution.problem);
      solution.setText(currentSolution.solution);
      if (currentSolution.approved.equals("Y")) {
        approved.setImageResource(R.drawable.approved);
      } else {
        approved.setImageResource(R.drawable.unapproved);
      }

      rowContainer.setOnClickListener(new ClickHandler(currentSolution));
      category.setOnClickListener(new ClickHandler(currentSolution));
      problem.setOnClickListener(new ClickHandler(currentSolution));
      solution.setOnClickListener(new ClickHandler(currentSolution));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return rowView;
  }

  public void callback(Solution solution) { }
}