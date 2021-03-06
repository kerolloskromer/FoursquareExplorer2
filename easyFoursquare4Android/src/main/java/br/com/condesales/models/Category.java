package br.com.condesales.models;

public class Category {

    private String id;

    private String name;

    private String pluralName;

    private String shortName;
    private Icon icon;

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }
    //TODO Icon model commented. It is currently left a side while foursquare api diverges in some venue responses.
    /*private Icon icon;
    public Icon getIcon() {
		return icon;
	}*/

    private String[] parents;

    private boolean primary;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPluralName() {
        return pluralName;
    }

    public String[] getParents() {
        return parents;
    }

    public boolean isPrimary() {
        return primary;
    }


    public String getShortName() {
        return shortName;
    }

    public class Icon {
        private String prefix;
        private String suffix;

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public String getSuffix() {
            return suffix;
        }

        public void setSuffix(String suffix) {
            this.suffix = suffix;
        }
    }
}
