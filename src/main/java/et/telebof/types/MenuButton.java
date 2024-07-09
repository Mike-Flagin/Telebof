package et.telebof.types;

import et.telebof.enums.MenuButtonType;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;

public class MenuButton implements Serializable {
    public String type;
    public WebAppInfo web_app;

    public MenuButton(@NotNull MenuButtonType type) {
        this.type = type.name().toLowerCase();
    }

    public MenuButton webApp(WebAppInfo web_app) {
        this.web_app = web_app;
        return this;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        MenuButton that = (MenuButton) object;
        return Objects.equals(type, that.type) && Objects.equals(web_app, that.web_app);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, web_app);
    }
}
