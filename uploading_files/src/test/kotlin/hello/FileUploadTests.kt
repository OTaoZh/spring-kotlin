package hello

import hello.storage.StorageService
import org.hamcrest.Matchers
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.nio.file.Paths
import java.util.stream.Stream

/**
 * Created by tao on 17-3-7.
 */
@RunWith(SpringRunner::class)
@AutoConfigureMockMvc
@SpringBootTest
class FileUploadTests {
    @Autowired
    var mvc: MockMvc? = null

    @MockBean
    var storageService: StorageService? = null

    @Test
    fun shouldListAllFiles(){
        given(this.storageService?.loadAll())
                .willReturn(Stream.of(Paths.get("first.txt"), Paths.get("second.txt")))

        this.mvc?.perform(get("/"))?.andExpect(status().isOk)?.andExpect(model().attribute("files", Matchers.contains("http://localhost/files/first.txt", "http://localhost/files/second.txt")))
    }

    @Test
    fun shouldSaveUploadedFile() {
        val multipartFile = MockMultipartFile("file", "test.txt", "text/plain", "Spring Framework".toByteArray())
        this.mvc?.perform(fileUpload("/").file(multipartFile))?.andExpect(status().isFound)?.andExpect(header().string("Location", "/"))
        then(this.storageService).should()?.store(multipartFile)
    }
}
