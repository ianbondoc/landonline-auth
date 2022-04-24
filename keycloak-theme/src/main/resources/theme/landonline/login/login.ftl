<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="robots" content="noindex, nofollow">

    <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons+Round">
    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Fira+Sans:300,400,500,600&amp;display=swap">
    <link rel="stylesheet"
          href="https://fonts.googleapis.com/css?family=Open+Sans:300,400,500,600,700&amp;display=swap">
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Indie+Flower&amp;display=swap">

    <#if properties.meta?has_content>
        <#list properties.meta?split(' ') as meta>
            <meta name="${meta?split('==')[0]}" content="${meta?split('==')[1]}"/>
        </#list>
    </#if>
    <title>Landonline Login</title>
    <link rel="icon" href="${url.resourcesPath}/img/favicon.ico"/>
    <#if properties.stylesCommon?has_content>
        <#list properties.stylesCommon?split(' ') as style>
            <link href="${url.resourcesCommonPath}/${style}" rel="stylesheet"/>
        </#list>
    </#if>
    <#if properties.styles?has_content>
        <#list properties.styles?split(' ') as style>
            <link href="${url.resourcesPath}/${style}" rel="stylesheet"/>
        </#list>
    </#if>
    <#if properties.scripts?has_content>
        <#list properties.scripts?split(' ') as script>
            <script src="${url.resourcesPath}/${script}" type="text/javascript"></script>
        </#list>
    </#if>
    <#if scripts??>
        <#list scripts as script>
            <script src="${script}" type="text/javascript"></script>
        </#list>
    </#if>
</head>
<body class="page">
<div class="image-section"></div>
<div class="form-section">
    <form id="kc-form-login" onsubmit="login.disabled = true; return true;" action="${url.loginAction}"
          method="post">
        <div class="form-column">
            <div class="header-row">
                <h1>${msg("formHeader")}</h1>
            </div>

            <div class="input-row">
                <label for="username" class="${properties.kcLabelClass!}">${msg("usernameLabel")}</label>

                <input tabindex="1" id="username" class="${properties.kcInputClass!}" name="username"
                       value="${(login.username!'')}" type="text" autofocus autocomplete="off"
                       aria-invalid="<#if messagesPerField.existsError('username','password')>true</#if>"
                />

                <#if messagesPerField.existsError('username','password')>
                    <span id="input-error" class="${properties.kcInputErrorMessageClass!}"
                          aria-live="polite">
                                    ${kcSanitize(messagesPerField.getFirstError('username','password'))?no_esc}
                            </span>
                </#if>

            </div>

            <div class="input-row">
                <label for="password" class="${properties.kcLabelClass!}">${msg("passwordLabel")}</label>

                <input tabindex="2" id="password" class="${properties.kcInputClass!}" name="password"
                       type="password" autocomplete="off"
                       aria-invalid="<#if messagesPerField.existsError('username','password')>true</#if>"
                />
            </div>

            <div class="terms-row">
                ${msg("termsAndConditions")?no_esc}
            </div>

            <div class="buttons-row">
                <input type="hidden" id="id-hidden-input" name="credentialId"
                       <#if auth.selectedCredential?has_content>value="${auth.selectedCredential}"</#if>/>

                <button tabindex="3" class="button button-primary" id="kc-login"
                        type="submit">${msg("loginButton")}</button>
                <button tabindex="4" class="button button-secondary" id="linz-login"
                        type="submit">${msg("linzLoginButton")}</button>
            </div>

            <div class="notices-row">
                <span class="info-icon">
                    <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path
                                d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2Zm0 15c-.55 0-1-.45-1-1v-4c0-.55.45-1 1-1s1 .45 1 1v4c0 .55-.45 1-1 1Zm1-8h-2V7h2v2Z"></path></svg>
                </span>
                ${msg("notices")?no_esc}
            </div>

            <div class="support-row">
                ${msg("support")?no_esc}
            </div>
        </div>
    </form>
</div>
</body>
</html>