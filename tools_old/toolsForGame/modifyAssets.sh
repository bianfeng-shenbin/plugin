SELECTED_PLUGINS=(${NEED_PUBLISH//:/ })
for plugin_name in ${SELECTED_PLUGINS[@]}
do
    SRC_ASSETS_DIR=${TARGET_ROOT}/${plugin_name}/android/ForAssets
    if [ -d "${SRC_ASSETS_DIR}" ]; then
        cp -rf "${SRC_ASSETS_DIR}"/* $1
    fi
done

exit 0
