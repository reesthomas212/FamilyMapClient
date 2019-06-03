package model;

public class Filter
{
    private boolean FilterMales = true;
    private boolean FilterFemales = true;
    private boolean FilterMomSide = true;
    private boolean FilterDadSide = true;

    public boolean isFilterMales() {
        return FilterMales;
    }

    public void setFilterMales(boolean filterMales) {
        FilterMales = filterMales;
    }

    public boolean isFilterFemales() {
        return FilterFemales;
    }

    public void setFilterFemales(boolean filterFemales) {
        FilterFemales = filterFemales;
    }

    public boolean isFilterMomSide() {
        return FilterMomSide;
    }

    public void setFilterMomSide(boolean filterMomSide) {
        FilterMomSide = filterMomSide;
    }

    public boolean isFilterDadSide() {
        return FilterDadSide;
    }

    public void setFilterDadSide(boolean filterDadSide) {
        FilterDadSide = filterDadSide;
    }
}
