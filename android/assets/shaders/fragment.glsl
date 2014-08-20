#ifdef GL_ES
precision mediump float;
#endif


#ifdef numDirLights
    struct dirLight {
        vec3 direction;
        vec4 color;
    };

    uniform dirLight u_dirLights[numDirLights];
#endif

#ifdef numPointLights
    struct pointLight {
        vec3 position;
        vec4 color;
    };

    uniform pointLight u_pointLights[numPointLights];
#endif

uniform float u_shininess;
uniform vec4 u_ambientColor;
uniform vec4 u_diffuseColor;
uniform vec4 u_specularColor;

varying vec3 v_normal, v_viewDirection;
varying vec3 v_worldSpacePosition;

void main() {
    vec3 normal = normalize(v_normal);
    vec3 lightDirection;
    vec4 diffuseLight = vec4(0);
    vec4 specularLight = vec4(0);
    #ifdef numDirLights
        for(int i=0; i<numDirLights; i++) {
            lightDirection = - normalize(u_dirLights[i].direction);
            vec4 value = u_dirLights[i].color * clamp(dot(normal, lightDirection), 0.0, 1.0);
            diffuseLight += value;
            float halfDotView = max(0.0, dot(normal, normalize(lightDirection + v_viewDirection)));
            specularLight += value * max(0.0, pow(halfDotView, u_shininess));
        }
    #endif

    #ifdef numPointLights
        for(int i=0; i<numPointLights; i++) {
            lightDirection = normalize(u_pointLights[i].position - v_worldSpacePosition);
            vec4 value = u_pointLights[i].color * clamp(dot(normal, lightDirection), 0.0, 1.0);
            diffuseLight += value;
            float halfDotView = max(0.0, dot(normal, normalize(lightDirection + v_viewDirection)));
            specularLight += value * max(0.0, pow(halfDotView, u_shininess));
        }
    #endif

    gl_FragColor = u_ambientColor * u_diffuseColor + u_diffuseColor * diffuseLight + u_specularColor * specularLight;
}