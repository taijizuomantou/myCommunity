$(function(){
    $("#uploadForm").submit(upload);
});
function upload(){
                var Bucket = $("#bucket").val();;
                var Region =$("#region").val();
                var MyKey =$("#key").val();
	 	        var selectedFile = document.getElementById('head-image').files[0];

                var cos = new COS({
                    getAuthorization: function (options, callback) {
                        // 异步获取临时密钥
                        $.get(CONTEXT_PATH + "/user/getTempTencetnAuth", {
                        }, function (data) {
                        data = $.parseJSON(data)
                            var credentials = data && data.credentials;
                            if (!data || !credentials) return console.error('credentials invalid');
                            callback({
                                TmpSecretId: credentials.tmpSecretId,
                                TmpSecretKey: credentials.tmpSecretKey,
                                XCosSecurityToken: credentials.sessionToken,
                                // 建议返回服务器时间作为签名的开始时间，避免用户浏览器本地时间偏差过大导致签名错误
                                StartTime: data.startTime, // 时间戳，单位秒，如：1580000000
                                ExpiredTime: data.expiredTime, // 时间戳，单位秒，如：1580000900
                            });
                        });
                    }
                });
                cos.putObject({
                        Bucket: Bucket, /* 必须 */
                        Region: Region,     /* 存储桶所在地域，必须字段 */
                        Key: MyKey,              /* 必须 */
                        StorageClass: 'STANDARD',
                        Body: selectedFile, // 上传文件对象
                        onProgress: function(progressData) {
                            console.log(JSON.stringify(progressData));
                        }
                    }, function(err, data) {
                        if(data){
                        //更新头像访问路径
                        $.post(
                            CONTEXT_PATH + "/user/header/url",
                            {"fileName":MyKey},
                            function(data){
                                data = $.parseJSON(data);
                                if(data.code == 0){
                                    window.location.reload();
                                }
                                else{
                                    alert(data.msg);
                                }
                            }
                        );
                       }
                        console.log(err || data);
                    });
    return false;
}