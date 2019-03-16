package io.reciteapp.recite.csprofile;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import io.reciteapp.recite.R;
import java.util.List;

public class CsProfileCertificationAdapter extends RecyclerView.Adapter<CsProfileCertificationAdapter.ViewHolder> {

  //Store a member variable for the content
  private final List<String> list;
  private final Context context;

  public CsProfileCertificationAdapter(Context context, List<String> list) {
    this.context = context;
    this.list = list;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    LayoutInflater inflater = LayoutInflater.from(context);

    View view = inflater.inflate(R.layout.certification_list_item, parent, false);

    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    String list = this.list.get(position);
    holder.bind(list);
  }

  @Override
  public int getItemCount() {
    if (list != null) {
      return list.size();
    }
    return 0;
  }

  public class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

    final TextView tvCertificate;

    ViewHolder(View itemView) {
      super(itemView);
      tvCertificate = itemView.findViewById(R.id.content_tv);
      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
      //Do nothing
    }

    public void bind(String list) {
      tvCertificate.setText(list);
    }
  }
}
