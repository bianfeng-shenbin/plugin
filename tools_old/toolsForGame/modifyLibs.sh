SELECTED_PLUGINS=(${NEED_PUBLISH//:/ })

if [ -d $1 ];then
    echo "$1 already exists!"
else
    mkdir $1
    echo "$1 mkdir success!"
fi

if [ -d $1/armeabi ];then
    echo "$1 already exists!"
else
    mkdir $1/armeabi
    echo "$1 mkdir success!"
fi

if [ -d $1/x86 ];then
    echo "$1 already exists!"
else
    mkdir $1/x86
    echo "$1 mkdir success!"
fi

for plugin_name in ${SELECTED_PLUGINS[@]}
do
    SRC_LIBS_DIR=${TARGET_ROOT}/${plugin_name}/android/ForLibs
    if [ -d "${SRC_LIBS_DIR}" ]; then
        cp -rf "${SRC_LIBS_DIR}"/* $1
    fi
done

exit 0
