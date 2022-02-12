package com.frostsowner.magic.weather.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.fm.commons.logic.BeanFactory;
import com.frostsowner.magic.weather.R;
import com.frostsowner.magic.weather.handle.CitySearchHandler;

import java.util.List;

/**
 * @Todo
 * @Author desmond
 * @Date 2017/5/19
 * @Pacakge com.desmond.citypicker.ui
 */

public class CitySearchAdapter extends BaseAdapter implements Filterable
{
    private Context context;
    private List<String> list;
    private CitySearchHandler citySearch;

    public CitySearchAdapter(Context context){
        this.context = context;
        citySearch = BeanFactory.getBean(CitySearchHandler.class);
        setItems(citySearch.getTotalCityData());
    }

    public void setItems(List<String> list){
        if(list == null)return;
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount(){
        if(list == null)return 0;
        return list.size();
    }

    @Override
    public Object getItem(int position)
    {
        return list.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder;
        if (convertView == null)
        {
            viewHolder = new ViewHolder();
            convertView = View.inflate(context, android.R.layout.simple_list_item_1, null);
            viewHolder.value = (TextView) convertView.findViewById(android.R.id.text1);
            viewHolder.value.setTextColor(context.getResources().getColor(R.color.black));
            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();
        String cityName = list.get(position);
        viewHolder.value.setText(cityName);
        return convertView;
    }

    class ViewHolder
    {
        TextView value;
    }

    @Override
    public Filter getFilter()
    {
        return new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence constraint)
            {
                String s = constraint.toString().trim();
                FilterResults results = new FilterResults();
                if (s.length() == 0)
                    return results;

                List<String> citys = citySearch.search(constraint.toString().trim());
                results.count = citys.size();
                results.values = citys;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results)
            {
                CitySearchAdapter.this.list = (List<String>) results.values;
                if (results.count > 0)
                    notifyDataSetChanged();
                else
                    notifyDataSetInvalidated();
            }
        };
    }
}
