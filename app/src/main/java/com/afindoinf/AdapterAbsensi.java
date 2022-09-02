package com.afindoinf;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterAbsensi extends RecyclerView.Adapter<AdapterAbsensi.HolderDataAbsensi>
{
    private final Context context;
    public ArrayList<ModelAbsen> absenList;

    public AdapterAbsensi(Context context, ArrayList<ModelAbsen> absenList)
    {
        this.context = context;
        this.absenList = absenList;
    }

    @NonNull
    @Override
    public HolderDataAbsensi onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.list_absen,parent,false);
        return new HolderDataAbsensi(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderDataAbsensi holder, int position)
    {
        ModelAbsen modelAbsen = absenList.get(position);
        String tanggal        = modelAbsen.getTanggal();
        String keterlambatan  = modelAbsen.getKeterlambatan();
        String kategori       = modelAbsen.getKategori();

        holder.tanggal_absensi_Tv.setText(tanggal);
        holder.keterlambatan_Tv.setText(keterlambatan);
        holder.kategori_Tv.setText(kategori);
    }

    @Override
    public int getItemCount()
    {
        return absenList.size();
    }

    static class HolderDataAbsensi extends RecyclerView.ViewHolder
    {
        private final TextView tanggal_absensi_Tv;
        private final TextView keterlambatan_Tv;
        private final TextView kategori_Tv;

        public HolderDataAbsensi(@NonNull View itemView)
        {
            super(itemView);

            tanggal_absensi_Tv = itemView.findViewById(R.id.tanggal_absensi_Tv);
            keterlambatan_Tv   = itemView.findViewById(R.id.keterlambatan_Tv);
            kategori_Tv        = itemView.findViewById(R.id.kategori_Tv);
        }
    }
}
