package pt.tecnico.groove.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;

@Component
public class DemoUtils {
    @Autowired
    private DemoService demoService;

    public void resetDemoInfo() {
        demoService.resetDemoSongs();
        demoService.resetDemoOwners();
    }

    public static String readResource(String resourcePath) throws IOException {
        Resource resource = new ClassPathResource(resourcePath);
        byte[] bytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
        return new String(bytes);
    }
}
