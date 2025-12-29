package com.bloodbowlclub.lib.io.serialization.resolvers;

import com.bloodbowlclub.team_building.domain.team_staff.TeamStaff;
import com.bloodbowlclub.team_building.domain.team_staff.staff_detail.Apothecary;
import com.bloodbowlclub.team_building.domain.team_staff.staff_detail.Cheerleaders;
import com.bloodbowlclub.team_building.domain.team_staff.staff_detail.CoachAssistant;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Custom TypeIdResolver for TeamStaff polymorphic serialization.
 *
 * Manages the serialization of different staff types (Apothecary, Cheerleaders, CoachAssistant)
 * using stable string aliases instead of full class names.
 */
public class TeamStaffTypeIdResolver extends TypeIdResolverBase {

    private static final Map<String, Class<? extends TeamStaff>> ALIAS_TO_CLASS = new HashMap<>();
    private static final Map<Class<? extends TeamStaff>, String> CLASS_TO_ALIAS = new HashMap<>();

    static {
        register("staff.apothecary", Apothecary.class);
        register("staff.cheerleaders", Cheerleaders.class);
        register("staff.coach.assistant", CoachAssistant.class);
    }

    private static void register(String alias, Class<? extends TeamStaff> staffClass) {
        ALIAS_TO_CLASS.put(alias, staffClass);
        CLASS_TO_ALIAS.put(staffClass, alias);
    }

    public static String getAlias(Class<? extends TeamStaff> staffClass) {
        String alias = CLASS_TO_ALIAS.get(staffClass);
        if (alias == null) {
            throw new IllegalArgumentException(
                "No alias registered for staff type: " + staffClass.getName()
            );
        }
        return alias;
    }

    public static Class<? extends TeamStaff> getStaffClass(String alias) {
        Class<? extends TeamStaff> staffClass = ALIAS_TO_CLASS.get(alias);
        if (staffClass == null) {
            throw new IllegalArgumentException("Unknown staff type alias: '" + alias + "'");
        }
        return staffClass;
    }

    public static boolean isRegistered(String alias) {
        return ALIAS_TO_CLASS.containsKey(alias);
    }

    public static boolean isRegistered(Class<? extends TeamStaff> staffClass) {
        return CLASS_TO_ALIAS.containsKey(staffClass);
    }

    private JavaType baseType;

    @Override
    public void init(JavaType baseType) {
        this.baseType = baseType;
    }

    @Override
    public String idFromValue(Object value) {
        if (value == null) {
            return null;
        }
        return getAlias((Class<? extends TeamStaff>) value.getClass());
    }

    @Override
    public String idFromValueAndType(Object value, Class<?> suggestedType) {
        return idFromValue(value);
    }

    @Override
    public JavaType typeFromId(DatabindContext context, String id) {
        Class<? extends TeamStaff> staffClass = getStaffClass(id);
        return TypeFactory.defaultInstance().constructType(staffClass);
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.CUSTOM;
    }
}
