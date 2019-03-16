package io.reciteapp.recite.reload;

import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.R;
import io.reciteapp.recite.customview.DebouncedOnClickListener;
import io.reciteapp.recite.data.model.PackageListResponse;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

class ReloadAdapter extends RecyclerView.Adapter<ReloadAdapter.ViewHolder> {

  private final ReloadFragment fragment;
  private List<PackageListResponse> packageList;

  ReloadAdapter(ReloadFragment context, ArrayList<PackageListResponse> responses) {
    this.fragment = context;
    this.packageList = responses;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View v = LayoutInflater
        .from(parent.getContext()).inflate(R.layout.list_item_reload, parent, false);
    return new ViewHolder(v);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    final PackageListResponse data = packageList.get(holder.getAdapterPosition());
    holder.bind(data);

    holder.cardView.setOnClickListener(new DebouncedOnClickListener(Constants.defaultClickValue) {
      @Override
      public void onDebouncedClick(View v) {
        Timber.d("Item click");
        fragment.payment(data);
      }
    });
  }


  @Override
  public int getItemCount() {
    if (packageList != null) {
      return packageList.size();
    }
    return 0;
  }

  @Override
  public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
    super.onViewDetachedFromWindow(holder);
    //for avoid fast scrolling animation
    holder.itemView.clearAnimation();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    final CardView cardView;
    final TextView tvPrice;
    final TextView tvTime;
    final TextView tvValidity;

    ViewHolder(View itemView) {
      super(itemView);
      cardView = itemView.findViewById(R.id.cardView_background);
      tvPrice = itemView.findViewById(R.id.tv_price);
      tvTime = itemView.findViewById(R.id.tv_time);
      tvValidity = itemView.findViewById(R.id.tv_validity);
    }

    public void bind(PackageListResponse data) {
      DecimalFormat formatter = new DecimalFormat("#,###,###");
      String price = formatter.format(data.getPackagePrice());
      String newPrice = price.replace(",",".");

      tvPrice.setText(String.valueOf("Rp" + newPrice));
      tvTime.setText(data.getPackageName());
      tvValidity.setText(data.getPackageDescription());

      if (!data.isExpired()) {
        tvValidity.setPaintFlags(tvValidity.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
      }
    }
  }
}
